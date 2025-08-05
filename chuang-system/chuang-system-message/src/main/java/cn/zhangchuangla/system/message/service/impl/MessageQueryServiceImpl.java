package cn.zhangchuangla.system.message.service.impl;

import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.system.message.constant.MessageConstants;
import cn.zhangchuangla.system.message.model.bo.MessageReadStatusBo;
import cn.zhangchuangla.system.message.model.dto.UserMessageDto;
import cn.zhangchuangla.system.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.system.message.model.entity.SysMessage;
import cn.zhangchuangla.system.message.model.entity.SysUserMessageExt;
import cn.zhangchuangla.system.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.system.message.model.vo.user.UserMessageVo;
import cn.zhangchuangla.system.message.service.MessageQueryService;
import cn.zhangchuangla.system.message.service.SysMessageService;
import cn.zhangchuangla.system.message.service.UserMessageExtService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息查询服务实现类
 * 专门负责消息的查询相关操作，包括已读状态的查询
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageQueryServiceImpl implements MessageQueryService {

    private final SysMessageService sysMessageService;
    private final UserMessageExtService userMessageExtService;

    /**
     * 用户消息列表查询
     *
     * @param request 查询参数
     * @return 分页消息结果
     */
    @Override
    public Page<UserMessageDto> listUserMessageList(UserMessageListQueryRequest request) {
        Long userId = SecurityUtils.getUserId();
        Page<SysMessage> sysMessagePage = new Page<>(request.getPageNum(), request.getPageSize());
        sysMessagePage = sysMessageService.pageUserMessage(sysMessagePage, userId, request);

        // 获取当前页面消息的已读状态（优化：只查询当前页的消息）
        List<Long> currentPageMessageIds = sysMessagePage.getRecords().stream()
                .map(SysMessage::getId)
                .toList();

        List<MessageReadStatusBo> messageReadStatusBos = userMessageExtService.getMessageReadStatus(userId, currentPageMessageIds);

        // 将已读状态列表转换为Map，方便快速查找
        Map<Long, Integer> readStatusMap = messageReadStatusBos.stream()
                .collect(Collectors.toMap(MessageReadStatusBo::getMessageId, MessageReadStatusBo::getIsRead, (a, b) -> b));

        // 转换为DTO并设置已读状态
        List<UserMessageDto> userMessageDtos = sysMessagePage.getRecords().stream()
                .map(message -> {
                    UserMessageDto dto = new UserMessageDto();
                    BeanUtils.copyProperties(message, dto);
                    // 从map中获取已读状态，如果不存在，则默认为未读
                    dto.setIsRead(readStatusMap.getOrDefault(message.getId(), MessageConstants.StatusConstants.MESSAGE_UN_READ));
                    return dto;
                })
                .toList();

        // 构建返回结果
        Page<UserMessageDto> resultPage = new Page<>();
        resultPage.setCurrent(sysMessagePage.getCurrent());
        resultPage.setSize(sysMessagePage.getSize());
        resultPage.setTotal(sysMessagePage.getTotal());
        resultPage.setRecords(userMessageDtos);

        return resultPage;
    }

    /**
     * 用户消息详情查询
     *
     * @param messageId 消息ID
     * @return 用户消息详情
     */
    @Override
    public UserMessageVo getUserMessageDetail(Long messageId) {
        Long userId = SecurityUtils.getUserId();

        // 获取消息详情
        SysMessage sysMessage = sysMessageService.getCurrentUserMessage(userId, messageId);
        if (sysMessage == null) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "消息不存在");
        }

        // 异步标记为已读（优化：避免在查询方法中执行写操作）
        userMessageExtService.read(userId, messageId);

        UserMessageVo userMessageVo = new UserMessageVo();
        BeanUtils.copyProperties(sysMessage, userMessageVo);
        return userMessageVo;
    }

    /**
     * 获取用户消息已读状态
     *
     * @return 用户消息已读状态
     */
    @Override
    public UserMessageReadCountDto getUserMessageReadCount() {
        Long userId = SecurityUtils.getUserId();
        long userMessageCount = sysMessageService.getUserMessageCount(userId);
        if (userMessageCount == 0L) {
            return new UserMessageReadCountDto(userId, 0L, 0L);
        }
        LambdaQueryWrapper<SysUserMessageExt> eq = new LambdaQueryWrapper<SysUserMessageExt>().eq(SysUserMessageExt::getUserId, userId);
        long read = userMessageExtService.count(eq);
        long unRead = userMessageCount - read;
        return new UserMessageReadCountDto(userMessageCount, read, unRead);
    }

}
