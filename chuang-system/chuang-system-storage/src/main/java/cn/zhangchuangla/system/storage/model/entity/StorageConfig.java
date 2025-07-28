package cn.zhangchuangla.system.storage.model.entity;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 文件配置表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "storage_config")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageConfig extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
    private Boolean isPrimary;

    /**
     * 是否启用回收站(0不启用，1启用)
     */
    private Integer enableTrash;

}
