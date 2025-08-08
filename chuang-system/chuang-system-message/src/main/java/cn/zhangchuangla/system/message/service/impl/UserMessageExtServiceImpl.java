package cn.zhangchuangla.system.message.service.impl;

import cn.zhangchuangla.system.message.constant.MessageConstants;
import cn.zhangchuangla.system.message.mapper.UserMessageExtMapper;
import cn.zhangchuangla.system.message.model.bo.MessageReadStatusBo;
import cn.zhangchuangla.system.message.model.entity.SysUserMessageExt;
import cn.zhangchuangla.system.message.service.UserMessageExtService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
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
     * 批量标记消息为已读
     *
     * <p>该方法会检查消息是否已经被标记为已读，避免重复操作。
     * 对于未读的消息，会创建新的已读记录并设置当前时间为首次和最后阅读时间。</p>
     *
     * @param userId     用户ID，不能为空
     * @param messageIds 消息ID列表，不能为空或空集合
     * @return 操作结果，true表示成功，false表示失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean read(Long userId, List<Long> messageIds) {
        // 参数验证
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            log.warn("标记已读操作参数无效，用户ID: {}, 消息ID列表: {}", userId, messageIds);
            return false;
        }

        log.debug("开始批量标记消息为已读，用户ID: {}, 消息数量: {}", userId, messageIds.size());

        try {
            // 查询用户已读的消息，避免重复插入
            List<Long> alreadyReadIds = getAlreadyReadMessageIds(userId, messageIds);

            // 过滤出需要标记为已读的消息ID
            List<Long> unreadMessageIds = messageIds.stream()
                    .filter(id -> !alreadyReadIds.contains(id))
                    .collect(Collectors.toList());

            if (unreadMessageIds.isEmpty()) {
                log.debug("所有消息都已标记为已读，用户ID: {}", userId);
                return true;
            }

            // 创建已读记录
            List<SysUserMessageExt> readRecords = createReadRecords(userId, unreadMessageIds);

            // 批量保存
            boolean result = this.saveOrUpdateBatch(readRecords);

            if (result) {
                log.debug("批量标记已读成功，用户ID: {}, 新标记数量: {}", userId, unreadMessageIds.size());
            } else {
                log.error("批量标记已读失败，用户ID: {}, 消息ID列表: {}", userId, unreadMessageIds);
            }

            return result;

        } catch (Exception e) {
            log.error("批量标记已读操作异常，用户ID: {}, 消息ID列表: {}", userId, messageIds, e);
            throw e;
        }
    }

    /**
     * 获取用户已读的消息ID列表
     *
     * @param userId     用户ID
     * @param messageIds 待检查的消息ID列表
     * @return 已读的消息ID列表
     */
    private List<Long> getAlreadyReadMessageIds(Long userId, List<Long> messageIds) {
        return this.lambdaQuery()
                .eq(SysUserMessageExt::getUserId, userId)
                .eq(SysUserMessageExt::getIsRead, MessageConstants.StatusConstants.MESSAGE_IS_READ)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .select(SysUserMessageExt::getMessageId)
                .list()
                .stream()
                .map(SysUserMessageExt::getMessageId)
                .collect(Collectors.toList());
    }

    /**
     * 创建已读记录列表
     *
     * @param userId     用户ID
     * @param messageIds 消息ID列表
     * @return 已读记录列表
     */
    private List<SysUserMessageExt> createReadRecords(Long userId, List<Long> messageIds) {
        Date currentTime = new Date();

        return messageIds.stream()
                .map(messageId -> SysUserMessageExt.builder()
                        .userId(userId)
                        .messageId(messageId)
                        .isRead(MessageConstants.StatusConstants.MESSAGE_IS_READ)
                        .firstReadTime(currentTime)
                        .lastReadTime(currentTime)
                        .createTime(currentTime)
                        .updateTime(currentTime)
                        .build())
                .collect(Collectors.toList());
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

        // 置为未读，但保留 firstReadTime/lastReadTime 以便数据分析
        return lambdaUpdate()
                .eq(SysUserMessageExt::getUserId, userId)
                .in(SysUserMessageExt::getMessageId, messageIds)
                .set(SysUserMessageExt::getIsRead, MessageConstants.StatusConstants.MESSAGE_UN_READ)
                .set(SysUserMessageExt::getUpdateTime, new Date())
                .update();
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
     * 批量获取消息的已读状态
     *
     * <p>该方法会查询指定消息列表中每条消息的已读状态。
     * 如果某条消息没有已读记录，则认为该消息未读。</p>
     *
     * @param userId     用户ID，不能为空
     * @param messageIds 消息ID列表，不能为空或空集合
     * @return 消息已读状态列表，包含消息ID和对应的已读状态
     */
    @Override
    public List<MessageReadStatusBo> getMessageReadStatus(Long userId, List<Long> messageIds) {
        // 参数验证
        if (userId == null || messageIds == null || messageIds.isEmpty()) {
            log.debug("获取消息已读状态参数无效，用户ID: {}, 消息ID列表: {}", userId, messageIds);
            return Collections.emptyList();
        }

        log.debug("开始获取消息已读状态，用户ID: {}, 消息数量: {}", userId, messageIds.size());

        try {
            // 查询用户对这些消息的已读记录
            List<SysUserMessageExt> readRecords = lambdaQuery()
                    .eq(SysUserMessageExt::getUserId, userId)
                    .in(SysUserMessageExt::getMessageId, messageIds)
                    .select(SysUserMessageExt::getMessageId, SysUserMessageExt::getIsRead)
                    .list();

            // 转换为已读状态BO列表
            List<MessageReadStatusBo> result = readRecords.stream()
                    .map(this::convertToReadStatusBo)
                    .collect(Collectors.toList());

            log.debug("获取消息已读状态完成，用户ID: {}, 返回状态数量: {}", userId, result.size());
            return result;

        } catch (Exception e) {
            log.error("获取消息已读状态异常，用户ID: {}, 消息ID列表: {}", userId, messageIds, e);
            return Collections.emptyList();
        }
    }

    /**
     * 将SysUserMessageExt转换为MessageReadStatusBo
     *
     * @param messageExt 用户消息扩展记录
     * @return 消息已读状态BO
     */
    private MessageReadStatusBo convertToReadStatusBo(SysUserMessageExt messageExt) {
        MessageReadStatusBo statusBo = new MessageReadStatusBo();
        statusBo.setMessageId(messageExt.getMessageId());
        statusBo.setIsRead(messageExt.getIsRead());
        return statusBo;
    }

    /**
     * 获取已读消息数量
     *
     * @return 已读消息数量
     */
    @Override
    public long getReadMessageCount(Long userId) {
        return baseMapper.getReadMessageCount(userId);
    }
}
