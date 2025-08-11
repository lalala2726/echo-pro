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
 * 系统消息表实体
 *
 * @author Chuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("sys_message")
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
     * 是否删除
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
