package cn.zhangchuangla.system.core.model.entity;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告表
 *
 * @author Chuang
 */
@TableName(value = "sys_notice")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNotice extends BaseEntity {

    /**
     * 公告ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告类型（1通知 2公告）
     */
    private String noticeType;
}
