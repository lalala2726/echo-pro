package cn.zhangchuangla.message.mapper;

import cn.zhangchuangla.message.model.entity.UserSiteMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface UserSiteMessageMapper extends BaseMapper<UserSiteMessage> {

    /**
     * 设置消息已读
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    void isRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 批量设置消息已读
     *
     * @param ids    消息ID
     * @param userId 用户ID
     * @return 更新的记录数
     */
    int batchMarkAsRead(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    /**
     * 设置所有消息已读
     *
     * @return 将全部站内信标记已读
     */
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 批量删除站内信
     *
     * @param ids    站内信ID
     * @param userId 当前用户ID
     * @return 删除的记录数
     */
    int batchDeleteByMessageIds(@Param("ids") List<Long> ids, @Param("userId") Long userId);
}




