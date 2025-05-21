package cn.zhangchuangla.system.model.entity;

import cn.idev.excel.annotation.ExcelProperty;
import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_post")
@Data
public class SysPost extends BaseEntity {

    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    @ExcelProperty(value = "岗位ID", order = 1)
    private Integer postId;

    /**
     * 岗位编码
     */
    @ExcelProperty(value = "岗位编码", order = 2)
    private String postCode;

    /**
     * 岗位名称
     */
    @ExcelProperty(value = "岗位名称", order = 3)
    private String postName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @ExcelProperty(value = "状态", order = 4)
    private Integer status;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    private Integer isDeleted;
}
