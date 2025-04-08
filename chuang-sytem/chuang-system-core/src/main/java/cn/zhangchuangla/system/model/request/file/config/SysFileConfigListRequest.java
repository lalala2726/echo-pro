package cn.zhangchuangla.system.model.request.file.config;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文件列表请求类")
public class SysFileConfigListRequest extends BasePageRequest {

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storageName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storageKey;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storageType;

    /**
     * 存储值
     */
    @Schema(description = "存储值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storageValue;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
