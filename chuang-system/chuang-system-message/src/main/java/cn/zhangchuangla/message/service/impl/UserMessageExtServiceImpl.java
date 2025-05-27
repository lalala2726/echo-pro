package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.message.constant.MessageConstants;
import cn.zhangchuangla.message.mapper.UserMessageExtMapper;
import cn.zhangchuangla.message.model.entity.UserMessageExt;
import cn.zhangchuangla.message.service.UserMessageExtService;
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
public class UserMessageExtServiceImpl extends ServiceImpl<UserMessageExtMapper, UserMessageExt>
        implements UserMessageExtService {


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
                .eq(UserMessageExt::getUserId, userId)
                .eq(UserMessageExt::getIsRead, MessageConstants.StatusConstants.MESSAGE_IS_READ)
                .in(UserMessageExt::getMessageId, messageIds)
                .list()
                .stream()
                .map(UserMessageExt::getMessageId)
                .toList();

        // 过滤掉已读的消息
        List<UserMessageExt> userMessageExtList = messageIds.stream()
                .filter(id -> !alreadyReadIds.contains(id))
                .map(id -> UserMessageExt.builder()
                        .userId(userId)
                        .messageId(id)
                        .build())
                .toList();

        if (userMessageExtList.isEmpty()) {
            // 已全部读过
            return true;
        }

        userMessageExtList.forEach(userMessageExt -> {
            userMessageExt.setIsRead(MessageConstants.StatusConstants.MESSAGE_IS_READ);
            //todo 待完善,这边需要判断是否是第一次真实读,并且记录时间信息
            userMessageExt.setFirstReadTime(null);
            userMessageExt.setLastReadTime(null);
        });
        return this.saveOrUpdateBatch(userMessageExtList);
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

        return update(new LambdaQueryWrapper<UserMessageExt>()
                .eq(UserMessageExt::getUserId, userId)
                .in(UserMessageExt::getMessageId, messageIds));
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
                .eq(UserMessageExt::getUserId, userId)
                .eq(UserMessageExt::getMessageId, messageId)
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
                .eq(UserMessageExt::getUserId, userId)
                .in(UserMessageExt::getMessageId, messageIds)
                .list()
                .stream()
                .map(UserMessageExt::getMessageId)
                .toList();
    }
}
