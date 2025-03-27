package cn.zhangchuangla.message.service;

import cn.zhangchuangla.message.model.entity.UserSiteMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface UserSiteMessageService extends IService<UserSiteMessage> {

    /**
     * 设置已读,支持多条消息已读
     *
     * @param ids 消息ID
     */
    int isRead(List<Long> ids);


    /**
     * 设置全部已读
     *
     * @return 操作结果
     */
    int isReadAll();

    /**
     * 删除站内信,支持批量删除
     *
     * @param ids 消息ID
     * @return 影响行数
     */
    int deleteSiteMessage(List<Long> ids);

}
