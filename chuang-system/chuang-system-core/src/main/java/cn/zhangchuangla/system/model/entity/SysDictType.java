package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典类型表
 * @author zhangchuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict_type")
@Data
public class SysDictType extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型编码
     */
    private String dictType;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    private Integer status;

}
