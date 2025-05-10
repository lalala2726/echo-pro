package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典项表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict_item")
@Data
public class SysDictItem extends BaseEntity {

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
     * 回显样式
     */
    private String tag;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    private Integer status;

}
