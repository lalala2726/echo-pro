package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 文件配置表
 */
@TableName(value = "sys_file_config")
@Data
public class SysFileConfig {

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
    private Integer isDefault;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}
