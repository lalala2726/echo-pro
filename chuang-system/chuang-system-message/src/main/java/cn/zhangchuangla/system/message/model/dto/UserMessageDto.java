package cn.zhangchuangla.system.message.model.dto;

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
    private Integer isRead;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息级别
     */
    private String level;

    /**
     * 发送者姓名
     */
    private String senderName;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 发布时间
     */
    private Date publishTime;

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
