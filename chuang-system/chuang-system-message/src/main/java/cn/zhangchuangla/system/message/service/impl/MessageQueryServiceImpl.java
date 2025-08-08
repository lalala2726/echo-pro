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
import cn.zhangchuangla.system.message.service.UserMessageReadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 消息查询服务实现类
 * 专门负责消息的查询相关操作，包括已读状态的查询和统计
 *
 * <p>主要功能包括：</p>
 * <ul>
 *   <li>用户消息列表分页查询（支持已读/未读筛选）</li>
 *   <li>用户消息详情查询（自动标记已读）</li>
 *   <li>用户消息已读未读统计</li>
 * </ul>
 *
 * @author Chuang
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageQueryServiceImpl implements MessageQueryService {

    private final SysMessageService sysMessageService;
    private final UserMessageExtService userMessageExtService;
    private final UserMessageReadService userMessageReadService;

    /**
     * 分页查询用户消息列表
     *
     * <p>支持按已读状态筛选消息，并自动填充每条消息的已读状态</p>
     *
     * <p>查询逻辑：</p>
     * <ul>
     *   <li>如果指定已读状态筛选，先获取用户的已读消息ID列表，然后进行筛选查询</li>
     *   <li>如果不指定筛选条件，查询用户所有可见消息</li>
     *   <li>查询完成后，批量获取当前页消息的已读状态并填充到结果中</li>
     * </ul>
     *
     * @param request 查询参数，包含分页信息和筛选条件
     * @return 包含已读状态的用户消息分页结果
     * @throws ServiceException 当查询参数无效时抛出
     */
    @Override
    public Page<UserMessageDto> listUserMessageList(UserMessageListQueryRequest request) {
        // 参数验证
        validateQueryRequest(request);

        final Long currentUserId = SecurityUtils.getUserId();

        // 执行分页查询
        Page<SysMessage> messagePage = executeMessageQuery(request, currentUserId);

        // 如果没有查询到消息，直接返回空结果
        if (CollectionUtils.isEmpty(messagePage.getRecords())) {
            return buildEmptyResultPage(messagePage);
        }

        // 批量获取并填充已读状态
        List<UserMessageDto> messageWithReadStatus = buildMessageDtosWithReadStatus(
                messagePage.getRecords(), currentUserId);

        // 构建最终结果
        Page<UserMessageDto> resultPage = buildResultPage(messagePage, messageWithReadStatus);

        return resultPage;
    }

    /**
     * 验证查询请求参数
     *
     * @param request 查询请求参数
     * @throws ServiceException 当参数无效时抛出
     */
    private void validateQueryRequest(UserMessageListQueryRequest request) {
        if (Objects.isNull(request)) {
            throw new ServiceException(ResultCode.PARAM_ERROR, "查询参数不能为空");
        }
        if (request.getPageNum() < 1) {
            throw new ServiceException(ResultCode.PARAM_ERROR, "页码必须大于0");
        }
        if (request.getPageSize() < 1 || request.getPageSize() > 100) {
            throw new ServiceException(ResultCode.PARAM_ERROR, "每页大小必须在1-100之间");
        }
    }

    /**
     * 执行消息查询
     *
     * @param request 查询请求
     * @param userId  用户ID
     * @return 消息分页结果
     */
    private Page<SysMessage> executeMessageQuery(UserMessageListQueryRequest request, Long userId) {
        Page<SysMessage> messagePage = new Page<>(request.getPageNum(), request.getPageSize());

        // 根据是否有已读状态筛选条件选择不同的查询策略
        if (Objects.nonNull(request.getIsRead())) {
            return executeReadStatusFilteredQuery(messagePage, userId, request);
        } else {
            return sysMessageService.pageUserMessage(messagePage, userId, request);
        }
    }

    /**
     * 执行带已读状态筛选的查询
     *
     * @param messagePage 分页对象
     * @param userId      用户ID
     * @param request     查询请求
     * @return 筛选后的消息分页结果
     */
    private Page<SysMessage> executeReadStatusFilteredQuery(Page<SysMessage> messagePage,
                                                            Long userId,
                                                            UserMessageListQueryRequest request) {
        // 获取用户已读的消息ID列表
        List<Long> readMessageIds = getUserReadMessageIds(userId);

        if (Boolean.TRUE.equals(request.getIsRead())) {
            // 查询已读消息：如果没有已读消息，返回空结果
            if (CollectionUtils.isEmpty(readMessageIds)) {
                log.debug("用户 {} 没有已读消息", userId);
                // 返回空的分页对象
                return messagePage;
            }
            return sysMessageService.pageUserMessageIsRead(messagePage, userId, request, readMessageIds);
        } else {
            // 查询未读消息
            return sysMessageService.pageUserMessageIsUnRead(messagePage, userId, request, readMessageIds);
        }
    }

    /**
     * 获取用户已读的消息ID列表
     *
     * @param userId 用户ID
     * @return 已读消息ID列表
     */
    private List<Long> getUserReadMessageIds(Long userId) {
        LambdaQueryWrapper<SysUserMessageExt> queryWrapper = new LambdaQueryWrapper<SysUserMessageExt>()
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getIsRead, MessageConstants.StatusConstants.MESSAGE_IS_READ)
                .select(SysUserMessageExt::getMessageId);

        List<SysUserMessageExt> readMessageExts = userMessageExtService.list(queryWrapper);

        if (CollectionUtils.isEmpty(readMessageExts)) {
            return Collections.emptyList();
        }

        return readMessageExts.stream()
                .map(SysUserMessageExt::getMessageId)
                .collect(Collectors.toList());
    }

    /**
     * 构建包含已读状态的消息DTO列表
     *
     * @param messages 消息列表
     * @param userId   用户ID
     * @return 包含已读状态的消息DTO列表
     */
    private List<UserMessageDto> buildMessageDtosWithReadStatus(List<SysMessage> messages, Long userId) {
        // 提取当前页消息ID列表
        List<Long> messageIds = messages.stream()
                .map(SysMessage::getId)
                .collect(Collectors.toList());

        // 批量获取已读状态
        List<MessageReadStatusBo> readStatusList = userMessageExtService.getMessageReadStatus(userId, messageIds);

        // 构建已读状态映射表，提高查找效率
        Map<Long, Integer> readStatusMap = readStatusList.stream()
                .collect(Collectors.toMap(
                        MessageReadStatusBo::getMessageId,
                        MessageReadStatusBo::getIsRead,
                        // 处理重复key的情况
                        (existing, replacement) -> replacement
                ));

        // 转换为DTO并填充已读状态
        return messages.stream()
                .map(message -> convertToUserMessageDto(message, readStatusMap))
                .collect(Collectors.toList());
    }

    /**
     * 将SysMessage转换为UserMessageDto并填充已读状态
     *
     * @param message       系统消息
     * @param readStatusMap 已读状态映射表
     * @return 用户消息DTO
     */
    private UserMessageDto convertToUserMessageDto(SysMessage message, Map<Long, Integer> readStatusMap) {
        UserMessageDto dto = new UserMessageDto();
        BeanUtils.copyProperties(message, dto);

        // 设置已读状态，默认为未读
        Integer readStatus = readStatusMap.getOrDefault(message.getId(),
                MessageConstants.StatusConstants.MESSAGE_UN_READ);
        dto.setIsRead(readStatus);

        return dto;
    }

    /**
     * 构建空的结果页面
     *
     * @param originalPage 原始分页对象
     * @return 空的结果页面
     */
    private Page<UserMessageDto> buildEmptyResultPage(Page<SysMessage> originalPage) {
        Page<UserMessageDto> resultPage = new Page<>();
        resultPage.setCurrent(originalPage.getCurrent());
        resultPage.setSize(originalPage.getSize());
        resultPage.setTotal(0L);
        resultPage.setRecords(Collections.emptyList());
        return resultPage;
    }

    /**
     * 构建最终结果页面
     *
     * @param originalPage 原始分页对象
     * @param records      记录列表
     * @return 结果页面
     */
    private Page<UserMessageDto> buildResultPage(Page<SysMessage> originalPage, List<UserMessageDto> records) {
        Page<UserMessageDto> resultPage = new Page<>();
        resultPage.setCurrent(originalPage.getCurrent());
        resultPage.setSize(originalPage.getSize());
        resultPage.setTotal(originalPage.getTotal());
        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 获取用户消息详情并自动标记为已读
     *
     * <p>该方法会在返回消息详情的同时，自动将消息标记为已读状态。
     * 这是一个读写结合的操作，符合用户查看消息详情的业务逻辑。</p>
     *
     * @param messageId 消息ID，不能为空
     * @return 用户消息详情视图对象
     * @throws ServiceException 当消息ID为空、消息不存在或用户无权限访问时抛出
     */
    @Override
    public UserMessageVo getUserMessageDetail(Long messageId) {
        // 参数验证
        if (Objects.isNull(messageId)) {
            throw new ServiceException(ResultCode.PARAM_ERROR, "消息ID不能为空");
        }

        final Long currentUserId = SecurityUtils.getUserId();

        // 获取消息详情
        SysMessage message = sysMessageService.getCurrentUserMessage(currentUserId, messageId);
        if (Objects.isNull(message)) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "消息不存在或您无权限访问");
        }

        // 标记消息为已读（使用真实阅读，记录首次与最后阅读时间）
        try {
            userMessageReadService.realRead(currentUserId, messageId);
        } catch (Exception e) {
            log.error("标记消息已读失败，用户ID: {}, 消息ID: {}", currentUserId, messageId, e);
        }

        // 转换为视图对象
        UserMessageVo messageVo = new UserMessageVo();
        BeanUtils.copyProperties(message, messageVo);

        return messageVo;
    }

    /**
     * 获取用户消息已读未读统计信息
     *
     * <p>统计当前用户的消息总数、已读数量和未读数量。
     * 该方法会查询用户可见的所有消息进行统计。</p>
     *
     * @return 用户消息已读未读统计信息
     * @throws ServiceException 当获取用户信息失败时抛出
     */
    @Override
    public UserMessageReadCountDto getUserMessageReadCount() {
        final Long currentUserId = SecurityUtils.getUserId();

        try {
            // 获取用户消息总数
            long totalMessageCount = sysMessageService.getUserMessageCount(currentUserId);

            // 如果用户没有任何消息，直接返回全零统计
            if (totalMessageCount == 0L) {
                return new UserMessageReadCountDto(0L, 0L, 0L);
            }

            // 获取已读消息数量
            long readMessageCount = userMessageExtService.getReadMessageCount(currentUserId);

            // 计算未读消息数量
            long unreadMessageCount = totalMessageCount - readMessageCount;

            return new UserMessageReadCountDto(
                    totalMessageCount, readMessageCount, unreadMessageCount);

        } catch (Exception e) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "获取消息统计信息失败");
        }
    }
}
