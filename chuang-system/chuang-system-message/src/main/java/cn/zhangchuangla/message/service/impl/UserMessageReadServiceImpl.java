package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.message.mapper.UserMessageReadMapper;
import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.entity.UserMessageRead;
import cn.zhangchuangla.message.service.UserMessageReadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户消息已读状态服务实现类
 * 专门负责消息的已读/未读状态管理
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageReadServiceImpl extends ServiceImpl<UserMessageReadMapper, UserMessageRead>
        implements UserMessageReadService {

    /**
     * 标记已读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean read(Long userId, Long messageId) {
        return read(userId, List.of(messageId));
    }

    /**
     * 批量标记已读
     *
     * @param userId     用户ID
     * @param messageIds 批量消息ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean read(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            return false;
        }

        // 查询用户已读的消息，避免重复插入
        List<Long> alreadyReadIds = this.lambdaQuery()
                .eq(UserMessageRead::getUserId, userId)
                .in(UserMessageRead::getMessageId, messageIds)
                .list()
                .stream()
                .map(UserMessageRead::getMessageId)
                .toList();

        // 过滤掉已读的消息
        List<UserMessageRead> userMessageReads = messageIds.stream()
                .filter(id -> !alreadyReadIds.contains(id))
                .map(id -> UserMessageRead.builder()
                        .userId(userId)
                        .messageId(id)
                        .build())
                .toList();

        if (userMessageReads.isEmpty()) {
            // 已全部读过
            return true;
        }

        return this.saveBatch(userMessageReads);
    }

    /**
     * 标记未读
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
     * 批量标记未读
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unread(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            return false;
        }

        return this.remove(
                new LambdaQueryWrapper<UserMessageRead>()
                        .eq(UserMessageRead::getUserId, userId)
                        .in(UserMessageRead::getMessageId, messageIds));
    }


    /**
     * 判断消息是否已读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 是否已读
     */
    @Override
    public boolean isMessageRead(Long userId, Long messageId) {
        if (userId == null || messageId == null) {
            return false;
        }

        return this.lambdaQuery()
                .eq(UserMessageRead::getUserId, userId)
                .eq(UserMessageRead::getMessageId, messageId)
                .exists();
    }

    /**
     * 获取已读消息ID列表
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
        return this.lambdaQuery()
                .eq(UserMessageRead::getUserId, userId)
                .in(UserMessageRead::getMessageId, messageIds)
                .list()
                .stream()
                .map(UserMessageRead::getMessageId)
                .toList();
    }
}
