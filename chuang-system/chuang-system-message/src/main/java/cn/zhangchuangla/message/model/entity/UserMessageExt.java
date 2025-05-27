package cn.zhangchuangla.message.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户消息扩展表
 *
 * @author Chuang
 */
@TableName(value = "user_message_ext")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMessageExt {
    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否阅读(0未读 1已读)
     */
    private Integer isRead;

    /**
     * 首次阅读时间(真实阅读,而不是批量标记已读)
     */
    private Date firstReadTime;

    /**
     * 最后阅读时间(真实阅读,而不是批量标记已读)
     */
    private Date lastReadTime;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
