package cn.zhangchuangla.system.message.service;

import cn.zhangchuangla.system.message.model.bo.MessageReadStatusBo;
import cn.zhangchuangla.system.message.model.entity.SysUserMessageExt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户消息已读状态服务接口
 * 专门负责消息的已读/未读状态管理
 *
 * @author Chuang
 */
public interface UserMessageExtService extends IService<SysUserMessageExt> {

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

    /**
     * 批量获取消息已读状态
     *
     * @param userId     用户ID
     * @param messageIds 待查询的消息ID列表
     * @return 已读消息ID列表
     */
    List<MessageReadStatusBo> getMessageReadStatus(Long userId, List<Long> messageIds);

    /**
     * 获取用户已读消息数量
     *
     * @param userId 用户ID
     * @return 已读消息数量
     */
    long getReadMessageCount(Long userId);
}
