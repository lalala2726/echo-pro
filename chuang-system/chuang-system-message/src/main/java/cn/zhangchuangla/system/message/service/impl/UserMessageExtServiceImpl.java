package cn.zhangchuangla.system.message.service.impl;

import cn.zhangchuangla.system.message.constant.MessageConstants;
import cn.zhangchuangla.system.message.mapper.UserMessageExtMapper;
import cn.zhangchuangla.system.message.model.bo.MessageReadStatusBo;
import cn.zhangchuangla.system.message.model.entity.SysUserMessageExt;
import cn.zhangchuangla.system.message.service.UserMessageExtService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户消息已读状态服务实现类
 * 专门负责消息的已读/未读状态管理
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageExtServiceImpl extends ServiceImpl<UserMessageExtMapper, SysUserMessageExt>
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
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getIsRead, MessageConstants.StatusConstants.MESSAGE_IS_READ)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .list()
                .stream()
                .map(SysUserMessageExt::getMessageId)
                .toList();

        // 过滤掉已读的消息
        List<SysUserMessageExt> sysUserMessageExtList = messageIds.stream()
                .filter(id -> !alreadyReadIds.contains(id))
                .map(id -> SysUserMessageExt.builder()
                        .userId(userId)
                        .messageId(id)
                        .build())
                .toList();

        if (sysUserMessageExtList.isEmpty()) {
            // 已全部读过
            return true;
        }

        sysUserMessageExtList.forEach(userMessageExt -> {
            userMessageExt.setIsRead(MessageConstants.StatusConstants.MESSAGE_IS_READ);
            //todo 待完善,这边需要判断是否是第一次真实读,并且记录时间信息
            userMessageExt.setFirstReadTime(null);
            userMessageExt.setLastReadTime(null);
        });
        return this.saveOrUpdateBatch(sysUserMessageExtList);
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

        return update(new LambdaQueryWrapper<SysUserMessageExt>()
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds));
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
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getMessageId, messageId)
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
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .list()
                .stream()
                .map(SysUserMessageExt::getMessageId)
                .toList();
    }

    /**
     * 获取消息已读状态
     *
     * @param userId     用户ID
     * @param messageIds 消息ID列表
     * @return 消息已读状态列表
     */
    @Override
    public List<MessageReadStatusBo> getMessageReadStatus(Long userId, List<Long> messageIds) {
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysUserMessageExt> list = lambdaQuery().eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .list();

        return list.stream().map(sysUserMessageExt -> {
            MessageReadStatusBo messageReadStatusBo = new MessageReadStatusBo();
            messageReadStatusBo.setMessageId(sysUserMessageExt.getMessageId());
            messageReadStatusBo.setIsRead(sysUserMessageExt.getIsRead());
            return messageReadStatusBo;
        }).collect(Collectors.toList());
    }
}
