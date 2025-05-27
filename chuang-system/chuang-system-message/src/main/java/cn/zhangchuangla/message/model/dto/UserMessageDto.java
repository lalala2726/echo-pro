package cn.zhangchuangla.message.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/26 19:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMessageDto {

    /**
     * 消息ID
     */
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
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    private Integer level;

    /**
     * 发送者ID，NULL表示系统发送
     */
    private Long senderId;

    /**
     * 发送者姓名
     */
    private String senderName;

    /**
     * 目标类型：0-指定用户 1-角色用户 2-部门用户 3-全部用户
     */
    private Integer targetType;


    /**
     * 推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket
     */
    private Integer pushType;

    /**
     * 是否已发布：0-未发布 1-已发布
     */
    private Integer isPublished;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 定时发送时间，NULL表示立即发送
     */
    private Date scheduledTime;

    /**
     * 过期时间，NULL表示不过期
     */
    private Date expireTime;

    /**
     * 是否删除：0-未删除 1-已删除
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

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}
