package cn.zhangchuangla.message.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户与站内信对应表
 */
@TableName(value = "user_site_message")
@Data
public class UserSiteMessage {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 是否已读(0:未读,1:已读)
     */
    private Integer isRead;

    /**
     * 是否标星(0:否,1:是)
     */
    private Integer isStarred;

    /**
     * 是否删除(0:否,1:是)
     */
    private Integer isDeleted;

    /**
     * 阅读时间
     */
    private Date readAt;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
