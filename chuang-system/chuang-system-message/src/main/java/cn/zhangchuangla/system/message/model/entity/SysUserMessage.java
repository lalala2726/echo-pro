package cn.zhangchuangla.system.message.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户消息关联表
 *
 * @author Chuang
 */
@TableName(value = "sys_user_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserMessage {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 是否收藏：0-未收藏 1-已收藏
     */
    private Integer isStarred;

    /**
     * 收藏时间
     */
    private Date starredTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    private Integer isDeleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
