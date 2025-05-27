package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.message.model.dto.UserMessageDto;
import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.entity.UserMessageExt;
import cn.zhangchuangla.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.message.model.vo.UserMessageVo;
import cn.zhangchuangla.message.service.MessageQueryService;
import cn.zhangchuangla.message.service.SysMessageService;
import cn.zhangchuangla.message.service.UserMessageExtService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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

        // 获取当前页面消息的已读状态（优化：只查询当前页面的消息）
        List<Long> currentPageMessageIds = sysMessagePage.getRecords().stream()
                .map(SysMessage::getId)
                .toList();

        List<Long> readMessageIds = getReadMessageIds(userId, currentPageMessageIds);

        // 转换为DTO并设置已读状态
        List<UserMessageDto> userMessageDtos = sysMessagePage.getRecords().stream()
                .map(message -> {
                    UserMessageDto dto = new UserMessageDto();
                    BeanUtils.copyProperties(message, dto);
                    dto.setIsRead(readMessageIds.contains(message.getId()));
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
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "消息不存在");
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
            return new UserMessageReadCountDto(userId, 0L, 0L, 0L);
        }
        LambdaQueryWrapper<UserMessageExt> eq = new LambdaQueryWrapper<UserMessageExt>().eq(UserMessageExt::getUserId, userId);
        long read = userMessageExtService.count(eq);
        long unRead = userMessageCount - read;
        return new UserMessageReadCountDto(userId, userMessageCount, read, unRead);
    }


    /**
     * 获取已读消息ID
     *
     * @param userId     用户ID
     * @param messageIds 消息ID列表
     * @return 已读消息ID列表
     */
    @Override
    public List<Long> getReadMessageIds(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            return List.of();
        }

        return userMessageExtService.list(
                        new LambdaQueryWrapper<UserMessageExt>()
                                .eq(UserMessageExt::getUserId, userId)
                                .in(UserMessageExt::getMessageId, messageIds))
                .stream()
                .map(UserMessageExt::getMessageId)
                .toList();
    }
}
