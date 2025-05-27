package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.UserMessageExt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户消息已读状态服务接口
 * 专门负责消息的已读/未读状态管理
 *
 * @author Chuang
 */
public interface UserMessageExtService extends IService<UserMessageExt> {

    /**
     * 标记单个消息为已读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    boolean read(Long userId, Long messageId);

    /**
     * 批量标记消息为已读
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    boolean read(Long userId, List<Long> messageIds);

    /**
     * 标记单个消息为未读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    boolean unread(Long userId, Long messageId);

    /**
     * 批量标记消息为未读
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    boolean unread(Long userId, List<Long> messageIds);

    /**
     * 检查用户是否已读指定消息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 是否已读
     */
    boolean isMessageRead(Long userId, Long messageId);

    /**
     * 批量检查消息已读状态
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 已读的消息ID列表
     */
    List<Long> getReadMessageIds(Long userId, List<Long> messageIds);
}
