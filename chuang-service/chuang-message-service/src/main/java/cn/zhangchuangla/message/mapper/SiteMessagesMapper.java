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
    Page<SiteMessages> getCurrentUserSiteMessagesList(@Param("userId") Long userId, Page<SiteMessages> page, @Param("isReadInt") int isReadInt);

    /**
     * 保存消息
     *
     * @param siteMessages 消息
     * @return 返回当前消息ID
     */
    Long saveSiteMessage(SiteMessages siteMessages);
}




