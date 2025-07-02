package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.SysUserMessageExt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * 用户消息已读状态服务接口
 * 专门负责消息的已读/未读状态管理，区分真实阅读和批量标记
 *
 * @author Chuang
 */
public interface UserMessageReadService extends IService<SysUserMessageExt> {

    /**
     * 真实阅读单个消息（用户实际查看消息内容）
     * 会记录首次和最后阅读时间
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    boolean realRead(Long userId, Long messageId);

    /**
     * 批量真实阅读消息（用户实际查看消息内容）
     * 会记录首次和最后阅读时间
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    boolean realRead(Long userId, List<Long> messageIds);

    /**
     * 批量标记消息为已读（管理操作，不记录真实阅读时间）
     * 用于全部标记已读等批量操作场景
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    boolean batchMarkAsRead(Long userId, List<Long> messageIds);

    /**
     * 标记单个消息为未读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    boolean unread(Long userId, Long messageId);

    /**
     * 批量标记消息为未读,这个方法仅用于批量标记未读，不记录真实阅读时间
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
     * 检查用户是否真实阅读过指定消息（有阅读时间记录）
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 是否真实阅读过
     */
    boolean isMessageRealRead(Long userId, Long messageId);

    /**
     * 批量检查消息已读状态
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 已读的消息ID列表
     */
    List<Long> getReadMessageIds(Long userId, List<Long> messageIds);

    /**
     * 获取消息的首次阅读时间
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 首次阅读时间，未阅读返回null
     */
    Date getFirstReadTime(Long userId, Long messageId);

    /**
     * 获取消息的最后阅读时间
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 最后阅读时间，未阅读返回null
     */
    Date getLastReadTime(Long userId, Long messageId);
}
