package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.message.mapper.UserMessageReadMapper;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.entity.UserMessageRead;
import cn.zhangchuangla.message.service.SysMessageService;
import cn.zhangchuangla.message.service.UserMessageReadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class UserMessageReadServiceImpl extends ServiceImpl<UserMessageReadMapper, UserMessageRead>
        implements UserMessageReadService {

    private final SysMessageService sysMessageService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean read(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            return false;
        }

        // 只查询存在的消息
        List<SysMessage> sysMessages = sysMessageService.list(
                new LambdaQueryWrapper<SysMessage>().in(SysMessage::getId, messageIds)
        );
        List<Long> existMessageIds = sysMessages.stream()
                .map(SysMessage::getId)
                .toList();

        if (existMessageIds.isEmpty()) {
            return false;
        }

        // 查询用户已读的消息，避免重复插入
        List<Long> alreadyReadIds = this.lambdaQuery()
                .eq(UserMessageRead::getUserId, userId)
                .in(UserMessageRead::getMessageId, existMessageIds)
                .list()
                .stream()
                .map(UserMessageRead::getMessageId)
                .toList();

        // 过滤掉已读的消息
        List<UserMessageRead> userMessageReads = existMessageIds.stream()
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
        // 只查询存在的消息
        List<SysMessage> sysMessages = sysMessageService.list(
                new LambdaQueryWrapper<SysMessage>().in(SysMessage::getId, messageIds)
        );
        List<Long> existMessageIds = sysMessages.stream()
                .map(SysMessage::getId)
                .toList();

        if (existMessageIds.isEmpty()) {
            return false;
        }
        return this.remove(
                new LambdaQueryWrapper<UserMessageRead>()
                        .eq(UserMessageRead::getUserId, userId)
                        .in(UserMessageRead::getMessageId, existMessageIds)
        );
    }
}




