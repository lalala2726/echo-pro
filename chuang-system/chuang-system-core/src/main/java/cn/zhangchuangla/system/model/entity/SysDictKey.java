package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典类型表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict_key")
@Data
public class SysDictKey extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型编码
     */
    private String dictKey;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    private Integer status;

}
