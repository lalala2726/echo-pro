package cn.zhangchuangla.system.model.request.config;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysConfigListRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String configName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String configKey;

    /**
     * 参数键值
     */
    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String configValue;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
