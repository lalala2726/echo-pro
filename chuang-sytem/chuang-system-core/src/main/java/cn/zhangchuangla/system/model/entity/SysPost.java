package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_post")
@Data
public class SysPost extends BaseEntity {

    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Integer postId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    private Integer status;


    /**
     * 是否删除(0-未删除,1-已删除)
     */
    private Integer isDeleted;
}
