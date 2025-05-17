package cn.zhangchuangla.system.model.request.config;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统配置列表查询请求对象", description = "系统配置查询列表请求对象")
public class SysConfigListRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String configName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String configKey;

    /**
     * 参数键值
     */
    @Schema(description = "参数键值", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String configValue;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
