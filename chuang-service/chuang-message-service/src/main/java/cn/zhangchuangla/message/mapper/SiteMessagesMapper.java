package cn.zhangchuangla.message.mapper;

import cn.zhangchuangla.message.model.entity.SiteMessages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SiteMessagesMapper extends BaseMapper<SiteMessages> {

    /**
     * 获取当前用户消息列表
     *
     * @param userId    用户ID
     * @param page      分页对象
     * @param isReadInt 是否已读
     * @return 返回消息列表
     */
    Page<SiteMessages> getCurrentUserSiteMessagesList(@Param("userId") Long userId, Page<SiteMessages> page, @Param("isReadInt") Integer isReadInt);

    /**
     * 保存消息
     *
     * @param siteMessages 消息
     * @return 返回当前消息ID
     */
    int saveSiteMessage(SiteMessages siteMessages);

    /**
     * 获取当前用户站消息详情
     *
     * @param userId    当前用户ID
     * @param messageId 消息ID
     * @return 消息
     */
    SiteMessages getCurrentUserSiteMessageById(@Param("userId") Long userId, @Param("messageId") Long messageId);


}




