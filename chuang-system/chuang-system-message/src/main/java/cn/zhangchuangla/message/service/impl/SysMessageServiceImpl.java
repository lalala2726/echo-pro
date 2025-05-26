package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.message.mapper.SysMessageMapper;
import cn.zhangchuangla.message.model.dto.UserMessageDto;
import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.entity.UserMessageRead;
import cn.zhangchuangla.message.model.request.*;
import cn.zhangchuangla.message.service.SysMessageService;
import cn.zhangchuangla.message.service.UserMessageReadService;
import cn.zhangchuangla.mq.dto.MessageSendDTO;
import cn.zhangchuangla.mq.production.MessageProducer;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.entity.SysUserRole;
import cn.zhangchuangla.system.service.SysDeptService;
import cn.zhangchuangla.system.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 系统消息表Service实现
 * 新设计：在消息中添加部门ID，角色ID，当用户查询消息的时候，根据部门ID，角色ID，用户ID查询消息
 * 只有单独给没有标签的用户发送消息的时候才会使用用户消息对应表，这边避免对数据插入大量的数据，节省资源
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    private final SysMessageMapper sysMessageMapper;
    private final SysUserRoleService sysUserRoleService;
    private final SysDeptService sysDeptService;
    private final MessageProducer messageProducer;
    private final UserMessageReadService userMessageReadService;

    /**
     * 分页查询系统消息表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<SysMessage> listSysMessage(SysMessageQueryRequest request) {
        Page<SysMessage> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysMessageMapper.pageSysMessage(page, request);
    }

    /**
     * 根据ID查询系统消息表
     *
     * @param id ID
     * @return 系统消息表
     */
    @Override
    public SysMessage getSysMessageById(Long id) {
        return getById(id);
    }

    /**
     * 新增系统消息表
     *
     * @param request 新增请求参数
     * @return 结果
     */
    @Override
    public boolean addSysMessage(SysMessageAddRequest request) {
        SysMessage sysMessage = new SysMessage();
        BeanUtils.copyProperties(request, sysMessage);
        return save(sysMessage);
    }

    /**
     * 修改系统消息表
     *
     * @param request 修改请求参数
     * @return 结果
     */
    @Override
    public boolean updateSysMessage(SysMessageUpdateRequest request) {
        SysMessage sysMessage = new SysMessage();
        BeanUtils.copyProperties(request, sysMessage);
        return updateById(sysMessage);
    }

    /**
     * 批量删除系统消息表
     *
     * @param ids 需要删除的ID集合
     * @return 结果
     */
    @Override
    public boolean deleteSysMessageByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sysSendMessage(SysSendMessageRequest request) {
        return switch (request.getSendMethod()) {
            case Constants.MessageConstants.SEND_METHOD_USER -> sendMessageToUserId(request);
            case Constants.MessageConstants.SEND_METHOD_ROLE -> sendMessageToRoleId(request);
            case Constants.MessageConstants.SEND_METHOD_DEPT -> sendMessageToDeptId(request);
            case Constants.MessageConstants.SEND_METHOD_ALL -> sendMessageToAll(request);
            default -> false;
        };
    }

    /**
     * 发送消息到指定用户，此处将会使用消息队列处理
     *
     * @param userId  用户ID
     * @param message 消息
     * @return 操作结果
     */
    @Override
    public boolean sendMessageByUserId(List<Long> userId, SysMessage message) {
        if (userId == null || userId.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        if (userId.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户ID必须大于零");
        }

        // 先保存消息
        message.setTargetType(Constants.MessageConstants.SEND_METHOD_USER);
        message.setCreateTime(new Date());
        boolean save = save(message);
        if (!save) {
            return false;
        }

        // 使用消息队列异步处理用户消息记录
        try {
            MessageSendDTO messageSendDTO = MessageSendDTO.builder()
                    .messageId(message.getId())
                    .title(message.getTitle())
                    .content(message.getContent())
                    .messageType(message.getType())
                    .sendMethod(Constants.MessageConstants.SEND_METHOD_USER)
                    .userIds(userId)
                    .batchSize(500)
                    .build();

            messageProducer.sendUserMessage(messageSendDTO);
            log.info("用户消息发送到队列成功，消息ID: {}, 用户数量: {}", message.getId(), userId.size());
            return true;
        } catch (Exception e) {
            log.error("用户消息发送到队列失败，消息ID: {}", message.getId(), e);
            // 如果队列发送失败，回滚消息记录
            removeById(message.getId());
            throw new ServiceException("消息发送失败");
        }
    }

    /**
     * 获取用户消息列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<UserMessageDto> listUserMessageList(UserMessageListQueryRequest request) {
        Page<SysMessage> sysMessagePage = new Page<>(request.getPageNum(), request.getPageSize());
        Long userId = SecurityUtils.getUserId();
        sysMessagePage = sysMessageMapper.pageUserMessage(sysMessagePage, userId, request);


        //  获取用户已读消息ID
        new LambdaQueryWrapper<UserMessageRead>().eq(UserMessageRead::getUserId, userId);
        List<Long> readMessageIds = userMessageReadService.list().stream()
                .map(UserMessageRead::getMessageId)
                .toList();

        List<UserMessageDto> userMessageDtos = sysMessagePage.getRecords().stream()
                .map(message -> {
                    UserMessageDto dto = new UserMessageDto();
                    BeanUtils.copyProperties(message, dto);
                    dto.setIsRead(readMessageIds.contains(message.getId()));
                    return dto;
                })
                .toList();

        Page<UserMessageDto> resultPage = new Page<>();
        resultPage.setCurrent(sysMessagePage.getCurrent());
        resultPage.setSize(sysMessagePage.getSize());
        resultPage.setTotal(sysMessagePage.getTotal());
        resultPage.setRecords(userMessageDtos);
        return resultPage;
    }

    /**
     * 根据ID查询当前用户的消息
     *
     * @param id ID
     * @return 系统消息表
     */
    @Override
    public SysMessage getCurrentUserMessageById(Long id) {
        Long userId = SecurityUtils.getUserId();
        userMessageReadService.read(userId, id);
        SysMessage sysMessage = sysMessageMapper.getCurrentUserMessage(userId, id);
        if (sysMessage == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "消息不存在");
        }
        return sysMessage;
    }

    /**
     * 获取用户消息已读未读数量
     *
     * @return 用户消息已读未读数量
     */
    @Override
    public UserMessageReadCountDto getUserMessageReadCount() {
        Long userId = SecurityUtils.getUserId();
        return userMessageReadService.getUserReadMessageCount(userId);
    }

    /**
     * 标记消息已读
     *
     * @param ids 消息ID
     * @return 结果
     */
    @Override
    public boolean markMessageAsRead(List<Long> ids) {
        Long userId = SecurityUtils.getUserId();
        return userMessageReadService.read(userId, ids);
    }

    /**
     * 批量标记消息未读
     *
     * @param ids 批量标记未读的ID
     * @return 批量标记未读结果
     */
    @Override
    public boolean markMessageAsUnRead(List<Long> ids) {
        Long userId = SecurityUtils.getUserId();
        return userMessageReadService.unread(userId, ids);
    }

    /**
     * 查询当前用户的发送消息列表
     *
     * @param request 查询参数
     * @return 分页消息结果
     */
    @Override
    public Page<SysMessage> listUserSentMessageList(UserMessageListQueryRequest request) {
        Long userId = SecurityUtils.getUserId();
        Page<SysMessage> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysMessageMapper.pageUserSentMessage(page, userId, request);
    }

    /**
     * 用户发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    @Override
    public boolean userSendMessage(UserSendMessageRequest request) {
        //todo 待开发
        return false;
    }

    /**
     * 根据用户ID发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    private boolean sendMessageToUserId(SysSendMessageRequest request) {
        List<Long> receiveId = request.getReceiveId();
        if (receiveId == null || receiveId.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        MessageRequest message = request.getMessage();
        SysMessage sysMessage = new SysMessage();
        BeanUtils.copyProperties(message, sysMessage);
        return sendMessageByUserId(receiveId, sysMessage);
    }

    /**
     * 根据角色ID发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    private boolean sendMessageToRoleId(SysSendMessageRequest request) {
        List<Long> receiveId = request.getReceiveId();
        if (receiveId == null || receiveId.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }

        // 查询所有有效的角色ID
        LambdaQueryWrapper<SysUserRole> roleQueryWrapper = new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getRoleId, receiveId);
        List<Long> validRoleIds = sysUserRoleService.list(roleQueryWrapper)
                .stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .toList();

        // 判断是否有无效的角色ID
        if (validRoleIds.size() != receiveId.size()) {
            List<Long> invalidRoleIds = receiveId.stream()
                    .filter(id -> !validRoleIds.contains(id))
                    .toList();
            throw new ServiceException(String.format("无效的角色ID：%s，请刷新网页重新请求！", invalidRoleIds));
        }

        // 先保存消息
        SysMessage sysMessage = SysMessage.builder()
                .targetType(Constants.MessageConstants.SEND_METHOD_ROLE)
                .createTime(new Date())
                .build();
        BeanUtils.copyProperties(request.getMessage(), sysMessage);
        boolean save = save(sysMessage);
        if (!save) {
            return false;
        }

        // 使用消息队列异步处理角色消息记录
        try {
            MessageSendDTO messageSendDTO = MessageSendDTO.builder()
                    .messageId(sysMessage.getId())
                    .title(sysMessage.getTitle())
                    .content(sysMessage.getContent())
                    .messageType(sysMessage.getType())
                    .sendMethod(Constants.MessageConstants.SEND_METHOD_ROLE)
                    .roleIds(receiveId)
                    .build();

            messageProducer.sendRoleMessage(messageSendDTO);
            log.info("角色消息发送到队列成功，消息ID: {}, 角色数量: {}", sysMessage.getId(), receiveId.size());
            return true;
        } catch (Exception e) {
            log.error("角色消息发送到队列失败，消息ID: {}", sysMessage.getId(), e);
            // 如果队列发送失败，回滚消息记录
            removeById(sysMessage.getId());
            throw new ServiceException("角色消息发送失败");
        }
    }

    /**
     * 根据部门ID发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    private boolean sendMessageToDeptId(SysSendMessageRequest request) {
        List<Long> receiveId = request.getReceiveId();
        if (receiveId == null || receiveId.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "部门ID不能为空");
        }

        // 查询所有有效的部门ID
        LambdaQueryWrapper<SysDept> deptQueryWrapper = new LambdaQueryWrapper<SysDept>()
                .in(SysDept::getDeptId, receiveId);

        List<Long> validDeptIds = sysDeptService.list(deptQueryWrapper)
                .stream()
                .map(SysDept::getDeptId)
                .distinct()
                .toList();

        if (validDeptIds.size() != receiveId.size()) {
            List<Long> invalidDeptIds = receiveId.stream()
                    .filter(id -> !validDeptIds.contains(id))
                    .toList();
            throw new ServiceException(String.format("无效的部门ID：%s，请刷新网页重新请求！", invalidDeptIds));
        }

        // 先保存消息
        SysMessage sysMessage = SysMessage.builder()
                .targetType(Constants.MessageConstants.SEND_METHOD_DEPT)
                .createTime(new Date())
                .build();
        BeanUtils.copyProperties(request.getMessage(), sysMessage);
        boolean save = save(sysMessage);
        if (!save) {
            return false;
        }

        // 使用消息队列异步处理部门消息记录
        try {
            MessageSendDTO messageSendDTO = MessageSendDTO.builder()
                    .messageId(sysMessage.getId())
                    .title(sysMessage.getTitle())
                    .content(sysMessage.getContent())
                    .messageType(sysMessage.getType())
                    .sendMethod(Constants.MessageConstants.SEND_METHOD_DEPT)
                    .deptIds(receiveId)
                    .build();

            messageProducer.sendDeptMessage(messageSendDTO);
            log.info("部门消息发送到队列成功，消息ID: {}, 部门数量: {}", sysMessage.getId(), receiveId.size());
            return true;
        } catch (Exception e) {
            log.error("部门消息发送到队列失败，消息ID: {}", sysMessage.getId(), e);
            // 如果队列发送失败，回滚消息记录
            removeById(sysMessage.getId());
            throw new ServiceException("部门消息发送失败");
        }
    }

    /**
     * 给全部用户发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    private boolean sendMessageToAll(SysSendMessageRequest request) {
        SysMessage sysMessage = SysMessage.builder()
                .targetType(Constants.MessageConstants.SEND_METHOD_ALL)
                .createTime(new Date())
                .build();
        BeanUtils.copyProperties(request.getMessage(), sysMessage);
        // 发送给全部用户无需设置用户消息对应表，直接保存消息即可
        return save(sysMessage);
    }
}
