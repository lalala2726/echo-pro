package cn.zhangchuangla.storage.model.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件配置表
 */
@Data
@Schema(description = "文件配置列表视图")
public class SysFileConfigListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String storageName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名")
    private String storageKey;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 存储值
     */
    @Schema(description = "存储值")
    private String storageValue;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
