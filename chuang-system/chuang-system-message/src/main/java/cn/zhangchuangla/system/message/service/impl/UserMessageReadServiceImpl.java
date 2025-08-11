package cn.zhangchuangla.system.message.service.impl;

import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.system.message.constant.MessageConstants;
import cn.zhangchuangla.system.message.mapper.UserMessageExtMapper;
import cn.zhangchuangla.system.message.model.entity.SysUserMessageExt;
import cn.zhangchuangla.system.message.service.UserMessageReadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户消息已读状态服务实现类
 * 专门负责消息的已读/未读状态管理，区分真实阅读和批量标记
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageReadServiceImpl extends ServiceImpl<UserMessageExtMapper, SysUserMessageExt>
        implements UserMessageReadService {

    /**
     * 真实阅读单个消息（用户实际查看消息内容）
     * 会记录首次和最后阅读时间
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean realRead(Long userId, Long messageId) {
        return realRead(userId, List.of(messageId));
    }

    /**
     * 批量真实阅读消息（用户实际查看消息内容）
     * 会记录首次和最后阅读时间
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean realRead(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            throw new ParamException("参数错误");
        }

        // 查询已存在的记录
        List<SysUserMessageExt> existingRecords = lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .list();

        // 处理不存在的 messageId
        List<Long> newMessageIds = new ArrayList<>(messageIds);
        if (!existingRecords.isEmpty()) {
            newMessageIds.removeAll(existingRecords.stream()
                    .map(SysUserMessageExt::getMessageId)
                    .toList());
        }

        Date now = new Date();

        // 创建新记录
        List<SysUserMessageExt> toCreate = newMessageIds.stream()
                .map(messageId -> SysUserMessageExt.builder()
                        .userId(userId)
                        .messageId(messageId)
                        .isRead(MessageConstants.StatusConstants.MESSAGE_IS_READ)
                        .firstReadTime(now)
                        .lastReadTime(now)
                        .createTime(now)
                        .updateTime(now)
                        .build())
                .collect(Collectors.toList());

        // 保存新记录
        if (!toCreate.isEmpty() && !saveBatch(toCreate)) {
            log.error("批量保存真实阅读记录失败: userId={}, messageIds={}", userId, newMessageIds);
            return false;
        }

        // 更新已存在记录（如果需要）
        if (!existingRecords.isEmpty()) {
            boolean updated = existingRecords.stream().allMatch(record -> {
                record.setIsRead(MessageConstants.StatusConstants.MESSAGE_IS_READ);
                record.setUpdateTime(now);

                // 只更新首次阅读时间，如果它尚未设置
                if (record.getFirstReadTime() == null) {
                    record.setFirstReadTime(now);
                }

                record.setLastReadTime(now);
                return updateById(record);
            });

            if (!updated) {
                log.warn("部分已有消息记录更新阅读状态失败: userId={}, messageIds={}", userId,
                        existingRecords.stream().map(SysUserMessageExt::getMessageId).collect(Collectors.toList()));
                // 根据业务需求决定是否返回false
            }
        }

        log.info("真实阅读操作完成: userId={}, total={}, created={}, updated={}",
                userId, messageIds.size(), toCreate.size(), existingRecords.size());
        return true;
    }

    /**
     * 批量标记消息为已读（管理操作，不记录真实阅读时间）
     * 用于全部标记已读等批量操作场景
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkAsRead(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            throw new ParamException("参数错误");
        }

        Date now = new Date();

        // 查询用户已有的阅读记录
        List<Long> existingMessageIds = this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .list()
                .stream()
                .map(SysUserMessageExt::getMessageId)
                .collect(Collectors.toList());

        // 更新已存在的记录为已读状态（不修改阅读时间）
        if (!existingMessageIds.isEmpty()) {
            boolean updateResult = this.lambdaUpdate()
                    .eq(SysUserMessageExt::getUserId, userId)
                    .in(SysUserMessageExt::getMessageId, existingMessageIds)
                    .set(SysUserMessageExt::getIsRead, 1)
                    .set(SysUserMessageExt::getUpdateTime, now)
                    // 注意：批量标记不修改 firstReadTime 和 lastReadTime
                    .update();

            if (!updateResult) {
                log.error("批量更新已读状态失败: userId={}, messageIds={}", userId, existingMessageIds);
                return false;
            }
        }

        // 为不存在的消息创建新的已读记录（不设置阅读时间）
        List<Long> newMessageIds = messageIds.stream()
                .filter(id -> !existingMessageIds.contains(id))
                .collect(Collectors.toList());

        if (!newMessageIds.isEmpty()) {
            List<SysUserMessageExt> newRecords = newMessageIds.stream()
                    .map(messageId -> SysUserMessageExt.builder()
                            .userId(userId)
                            .messageId(messageId)
                            .isRead(1)
                            // 注意：批量标记不设置 firstReadTime 和 lastReadTime
                            .createTime(now)
                            .updateTime(now)
                            .build())
                    .collect(Collectors.toList());

            if (!this.saveBatch(newRecords)) {
                log.error("批量保存已读记录失败: userId={}, messageIds={}", userId, newMessageIds);
                return false;
            }
        }

        log.info("批量标记消息已读完成: userId={}, totalCount={}, updated={}, created={}",
                userId, messageIds.size(), existingMessageIds.size(), newMessageIds.size());

        return true;
    }

    /**
     * 标记单个消息为未读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unread(Long userId, Long messageId) {
        return unread(userId, List.of(messageId));
    }

    /**
     * 批量标记消息为未读
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unread(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            throw new ParamException("参数错误");
        }

        // 将已读状态设置为未读，但保留阅读时间记录（用于数据分析）
        boolean result = lambdaUpdate()
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .set(SysUserMessageExt::getIsRead, MessageConstants.StatusConstants.MESSAGE_UN_READ)
                .set(SysUserMessageExt::getUpdateTime, new Date())
                .update();

        new LambdaQueryWrapper<SysUserMessageExt>().eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds);

        log.info("批量标记消息未读: userId={}, messageIds={}, result={}", userId, messageIds, result);
        return result;
    }

    /**
     * 检查用户是否已读指定消息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 是否已读
     */
    @Override
    public boolean isMessageRead(Long userId, Long messageId) {
        if (userId == null || messageId == null) {
            throw new ParamException("参数错误");
        }

        return this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getMessageId, messageId)
                .eq(SysUserMessageExt::getIsRead, 1)
                .exists();
    }

    /**
     * 检查用户是否真实阅读过指定消息（有阅读时间记录）
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 是否真实阅读过
     */
    @Override
    public boolean isMessageRealRead(Long userId, Long messageId) {
        if (userId == null || messageId == null) {
            throw new ParamException("参数错误");
        }

        return this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getMessageId, messageId)
                .eq(SysUserMessageExt::getIsRead, 1)
                // 有首次阅读时间才算真实阅读
                .isNotNull(SysUserMessageExt::getFirstReadTime)
                .exists();
    }

    /**
     * 批量检查消息已读状态
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 已读的消息ID列表
     */
    @Override
    public List<Long> getReadMessageIds(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            return List.of();
        }

        return this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .eq(SysUserMessageExt::getIsRead, 1)
                .list()
                .stream()
                .map(SysUserMessageExt::getMessageId)
                .collect(Collectors.toList());
    }

    /**
     * 获取消息的首次阅读时间
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 首次阅读时间，未阅读返回null
     */
    @Override
    public Date getFirstReadTime(Long userId, Long messageId) {
        if (userId == null || messageId == null) {
            throw new ParamException("参数错误");
        }

        SysUserMessageExt record = this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getMessageId, messageId)
                .one();

        return record != null ? record.getFirstReadTime() : null;
    }

    /**
     * 获取消息的最后阅读时间
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 最后阅读时间，未阅读返回null
     */
    @Override
    public Date getLastReadTime(Long userId, Long messageId) {
        if (userId == null || messageId == null) {
            throw new ParamException("参数错误");
        }

        SysUserMessageExt record = this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getMessageId, messageId)
                .one();

        return record != null ? record.getLastReadTime() : null;
    }

}
