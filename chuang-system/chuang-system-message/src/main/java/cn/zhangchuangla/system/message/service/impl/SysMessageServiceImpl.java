package cn.zhangchuangla.system.message.service.impl;

import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.XssUtils;
import cn.zhangchuangla.common.mq.dto.MessageSendDTO;
import cn.zhangchuangla.common.mq.production.MessageProducer;
import cn.zhangchuangla.system.core.model.entity.SysDept;
import cn.zhangchuangla.system.core.model.entity.SysUserRole;
import cn.zhangchuangla.system.core.service.SysDeptService;
import cn.zhangchuangla.system.core.service.SysUserRoleService;
import cn.zhangchuangla.system.message.enums.MessageSendMethodEnum;
import cn.zhangchuangla.system.message.mapper.SysMessageMapper;
import cn.zhangchuangla.system.message.model.entity.SysMessage;
import cn.zhangchuangla.system.message.model.request.*;
import cn.zhangchuangla.system.message.model.vo.system.SysMessageVo;
import cn.zhangchuangla.system.message.service.SysMessageService;
import cn.zhangchuangla.system.message.service.SysUserMessageService;
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
 * 系统消息服务实现类
 * 专门负责消息的CRUD操作和发送
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

    // 批量发送消息数量
    private static final int BEACH_SEND_MESSAGE_QUANTITY = 500;
    private final SysMessageMapper sysMessageMapper;
    private final SysUserRoleService sysUserRoleService;
    private final SysDeptService sysDeptService;
    private final MessageProducer messageProducer;
    private final SysUserMessageService sysUserMessageService;

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
    public SysMessageVo getSysMessageById(Long id) {
        SysMessage sysMessage = getById(id);
        if (sysMessage == null) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "消息不存在");
        }
        SysMessageVo sysMessageVo = new SysMessageVo();
        // 如果消息发送目标类型不是"全部用户"，则需要处理目标ID的关联查询，否则不需要传入目标ID
        if (sysMessage.getTargetType() != null && !sysMessage.getTargetType().equals(MessageSendMethodEnum.ALL.getValue())) {
            List<Long> targetIds = sysUserMessageService.getRecipientIdsByMessageId(id);
            sysMessageVo.setTargetIds(targetIds);
        }
        BeanUtils.copyProperties(sysMessage, sysMessageVo);
        sysMessageVo.setPublishTime(sysMessage.getCreateTime());
        return sysMessageVo;
    }


    /**
     * 修改系统消息表
     *
     * @param request 修改请求参数
     * @return 结果
     */
    @Override
    public boolean updateSysMessage(SysMessageUpdateRequest request) {
        // XSS 清洗
        request.setTitle(XssUtils.sanitizeHtml(request.getTitle()));
        request.setContent(XssUtils.sanitizeHtml(request.getContent()));
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
        String senderName = SecurityUtils.getUsername();
        return sendMessage(request, senderName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean systemSendMessage(SysSendMessageRequest request) {
        return sendMessage(request, "系统");
    }

    /**
     * 根据发送方式分发消息
     */
    private boolean sendMessage(SysSendMessageRequest request, String senderName) {
        MessageSendMethodEnum method = request.getSendMethod();
        if (method == null) {
            throw new ParamException(ResultCode.PARAM_ERROR, "消息发送方式不能为空");
        }
        return switch (method) {
            case USER -> sendMessageToUserId(request, senderName);
            case ROLE -> sendMessageToRoleId(request, senderName);
            case DEPT -> sendMessageToDeptId(request, senderName);
            case ALL -> sendMessageToAll(request, senderName);
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
            throw new ParamException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }
        if (userId.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ParamException(ResultCode.PARAM_ERROR, "用户ID必须大于零");
        }

        // 先保存消息
        message.setTargetType(MessageSendMethodEnum.USER.getValue());
        message.setCreateTime(new Date());
        boolean save = save(message);
        if (!save) {
            return false;
        }

        // 使用消息队列异步处理用户消息记录
        try {
            MessageSendDTO messageSendDTO = MessageSendDTO.builder()
                    .messageId(message.getId())
                    .title(XssUtils.extractPlainText(message.getTitle()))
                    .content(XssUtils.extractPlainText(message.getContent()))
                    .messageType(message.getType())
                    .sendMethod("user")
                    .userIds(userId)
                    .batchSize(BEACH_SEND_MESSAGE_QUANTITY)
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
     * 根据用户ID发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    private boolean sendMessageToUserId(SysSendMessageRequest request, String senderName) {
        List<Long> receiveId = request.getReceiveId();
        if (receiveId == null || receiveId.isEmpty()) {
            throw new ParamException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }
        MessageRequest message = request.getMessage();
        // XSS 清洗
        message.setTitle(XssUtils.sanitizeHtml(message.getTitle()));
        message.setContent(XssUtils.sanitizeHtml(message.getContent()));
        SysMessage sysMessage = buildBaseSysMessage(message, senderName, MessageSendMethodEnum.USER.getValue());
        return sendMessageByUserId(receiveId, sysMessage);
    }

    /**
     * 根据角色ID发送消息
     *
     * @param request 发送消息请求参数
     * @return 结果
     */
    private boolean sendMessageToRoleId(SysSendMessageRequest request, String senderName) {
        List<Long> receiveId = request.getReceiveId();
        if (receiveId == null || receiveId.isEmpty()) {
            throw new ParamException(ResultCode.PARAM_ERROR, "角色ID不能为空");
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
        MessageRequest message = request.getMessage();
        message.setTitle(XssUtils.sanitizeHtml(message.getTitle()));
        message.setContent(XssUtils.sanitizeHtml(message.getContent()));
        SysMessage sysMessage = buildBaseSysMessage(message, senderName, MessageSendMethodEnum.ROLE.getValue());
        boolean save = save(sysMessage);
        if (!save) {
            return false;
        }

        // 使用消息队列异步处理角色消息记录
        try {
            MessageSendDTO messageSendDTO = MessageSendDTO.builder()
                    .messageId(sysMessage.getId())
                    .title(XssUtils.extractPlainText(sysMessage.getTitle()))
                    .content(XssUtils.extractPlainText(sysMessage.getContent()))
                    .messageType(sysMessage.getType())
                    .sendMethod("role")
                    .roleIds(receiveId)
                    .batchSize(BEACH_SEND_MESSAGE_QUANTITY)
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
    private boolean sendMessageToDeptId(SysSendMessageRequest request, String senderName) {
        List<Long> receiveId = request.getReceiveId();
        if (receiveId == null || receiveId.isEmpty()) {
            throw new ParamException(ResultCode.PARAM_ERROR, "部门ID不能为空");
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
        MessageRequest message = request.getMessage();
        message.setTitle(XssUtils.sanitizeHtml(message.getTitle()));
        message.setContent(XssUtils.sanitizeHtml(message.getContent()));
        SysMessage sysMessage = buildBaseSysMessage(message, senderName, MessageSendMethodEnum.DEPT.getValue());
        boolean save = save(sysMessage);
        if (!save) {
            return false;
        }

        // 使用消息队列异步处理部门消息记录
        try {
            MessageSendDTO messageSendDTO = MessageSendDTO.builder()
                    .messageId(sysMessage.getId())
                    .title(XssUtils.extractPlainText(sysMessage.getTitle()))
                    .content(XssUtils.extractPlainText(sysMessage.getContent()))
                    .messageType(sysMessage.getType())
                    .sendMethod("dept")
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
    private boolean sendMessageToAll(SysSendMessageRequest request, String senderName) {
        // XSS 清洗
        MessageRequest message = request.getMessage();
        message.setTitle(XssUtils.sanitizeHtml(message.getTitle()));
        message.setContent(XssUtils.sanitizeHtml(message.getContent()));
        SysMessage sysMessage = buildBaseSysMessage(message, senderName, MessageSendMethodEnum.ALL.getValue());
        // 发送给全部用户无需设置用户消息对应表，直接保存消息即可
        return save(sysMessage);
    }

    private SysMessage buildBaseSysMessage(MessageRequest message, String senderName, String targetType) {
        String typeEnum = message.getType().getValue();
        String levelEnum = message.getLevel().getValue();

        return SysMessage.builder()
                .targetType(targetType)
                .senderName(senderName)
                .createTime(new Date())
                .title(message.getTitle())
                .content(message.getContent())
                .type(typeEnum)
                .level(levelEnum)
                .build();
    }


    /**
     * 获取用户消息数量
     *
     * @param userId 用户ID
     * @return 用户消息数量
     */
    @Override
    public long getUserMessageCount(Long userId) {
        return sysMessageMapper.getUserMessageCount(userId);
    }

    /**
     * 获取用户消息列表
     *
     * @param sysMessagePage 分页参数
     * @param userId         用户ID
     * @param request        查询参数
     * @return 用户消息列表
     */
    @Override
    public Page<SysMessage> pageUserMessage(Page<SysMessage> sysMessagePage, Long userId, UserMessageListQueryRequest request) {
        return sysMessageMapper.pageUserMessage(sysMessagePage, userId, request);
    }

    /**
     * 分页查询用户已读消息
     *
     * @param sysMessagePage 分页参数
     * @param userId         用户ID
     * @param request        查询参数
     * @param messageIds     已读消息ID列表
     * @return 用户已读消息列表
     */
    @Override
    public Page<SysMessage> pageUserMessageIsRead(Page<SysMessage> sysMessagePage, Long userId, UserMessageListQueryRequest request, List<Long> messageIds) {
        return sysMessageMapper.pageUserMessageIsRead(sysMessagePage, userId, request, messageIds);
    }

    /**
     * 根据消息角色ID和消息ID获取用户消息详情
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 用户消息详情
     */
    @Override
    public SysMessage getCurrentUserMessage(Long userId, Long messageId) {
        return sysMessageMapper.getCurrentUserMessage(userId, messageId);
    }

    /**
     * 根据用户ID和消息ID批量获取消息详情
     *
     * @param userId     用户ID
     * @param messageIds 消息ID列表
     * @return 消息详情
     */
    @Override
    public List<SysMessage> listMessageWithUserIdAndMessageId(Long userId, List<Long> messageIds) {
        return sysMessageMapper.listMessageWithUserIdAndMessageId(userId, messageIds);
    }

    /**
     * 批量删除消息
     *
     * @param ids 消息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessages(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    /**
     * 获取用户未读消息列表
     *
     * @param sysMessagePage 分页参数
     * @param userId         用户ID
     * @param request        查询参数
     * @param messageIds     已读消息ID列表（用于排除）
     * @return 用户未读消息列表
     */
    @Override
    public Page<SysMessage> pageUserMessageIsUnRead(Page<SysMessage> sysMessagePage, Long userId, UserMessageListQueryRequest request, List<Long> messageIds) {
        return sysMessageMapper.pageUserMessageIsUnRead(sysMessagePage, userId, request, messageIds);
    }
}
