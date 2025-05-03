package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统字典项表
 *
 */
@TableName(value = "sys_dict_item")
@Data
public class SysDictItem {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属字典类型编码
     */
    private String dictType;

    /**
     * 字典项名称
     */
    private String itemLabel;

    /**
     * 字典项值
     */
    private String itemValue;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * 状态：0启用，1禁用
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}