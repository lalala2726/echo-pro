package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.SiteMessages;
import cn.zhangchuangla.message.model.request.SiteMessageListRequest;
import cn.zhangchuangla.message.model.request.SiteMessageRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * @author zhangchuang
 */
public interface SiteMessagesService extends IService<SiteMessages> {

    /**
     * 获取当前用户站内信列表
     *
     * @return 返回当前用户的站内信列表
     */
    Page<SiteMessages> getCurrentUserSiteMessagesList(SiteMessageListRequest request);

    /**
     * 发送站内信
     *
     * @param siteMessageRequest 站内信信息
     * @return 操作结果
     */
    boolean sendSiteMessage(SiteMessageRequest siteMessageRequest);

    /**
     * 根据id获取站内信
     *
     * @param id 消息ID
     * @return 消息
     */
    SiteMessages getSiteMessageById(Long id);

    /**
     * 设置已读,支持多条消息已读
     *
     * @param ids 消息ID
     */
    int isRead(List<Long> ids);


}
