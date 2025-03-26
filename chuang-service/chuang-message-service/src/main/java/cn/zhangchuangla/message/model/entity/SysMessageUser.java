package cn.zhangchuangla.message.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 消息接收表
 */
@TableName(value = "sys_message_user")
@Data
public class SysMessageUser {
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 阅读状态(0:未读 1:已读)
     */
    private Integer readStatus;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 是否删除(0:未删除 1:已删除)
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
