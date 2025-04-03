package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件配置表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file_config")
@Data
public class SysFileConfig extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 参数名称
     */
    private String storageName;

    /**
     * 参数键名
     */
    private String storageKey;

    /**
     * 存储值
     */
    private String storageValue;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 是否默认
     */
    private Integer isMaster;

}
