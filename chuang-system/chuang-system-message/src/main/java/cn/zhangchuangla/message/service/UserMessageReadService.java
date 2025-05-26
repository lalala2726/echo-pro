package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.entity.UserMessageRead;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface UserMessageReadService extends IService<UserMessageRead> {

    /**
     * 读取消息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 读取结果
     */
    boolean read(Long userId, Long messageId);

    /**
     * 批量读取消息
     *
     * @param userId     用户ID
     * @param messageIds 消息ID集合
     * @return 操作结果
     */
    boolean read(Long userId, List<Long> messageIds);

    /**
     * 标记消息为未读
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 操作结果
     */
    boolean unread(Long userId, Long messageId);

    /**
     * 批量标记消息为未读
     *
     * @param userId    用户ID
     * @param messageId 消息ID集合
     * @return 操作结果
     */
    boolean unread(Long userId, List<Long> messageId);

    /**
     * 获取用户读取消息数量
     *
     * @param userId 用户ID
     * @return 已读消息列表
     */
    UserMessageReadCountDto getUserReadMessageCount(Long userId);


}
