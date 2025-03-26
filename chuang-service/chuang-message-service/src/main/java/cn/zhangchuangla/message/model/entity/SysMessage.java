package cn.zhangchuangla.message.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统消息表
 */
@TableName(value = "sys_message")
@Data
public class SysMessage {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型(0:系统消息 1:通知公告 2:私信)
     */
    private Integer messageType;

    /**
     * 重要等级(0:普通 1:重要 2:紧急)
     */
    private Integer importanceLevel;

    /**
     * 消息状态(0:正常 1:删除)
     */
    private Integer status;

    /**
     * 发送者ID(系统消息为空)
     */
    private Long senderId;

    /**
     * 发送者名称(系统消息为"系统")
     */
    private String senderName;

    /**
     * 是否广播(0:否 1:是)
     */
    private Integer isBroadcast;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;
}
