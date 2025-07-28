package cn.zhangchuangla.system.core.model.request.config;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置列表查询请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统配置列表查询请求对象", description = "系统配置查询列表请求对象")
public class SysConfigQueryRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "integer", example = "1")
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", type = "string", example = "系统名称")
    private String configName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名", type = "string", example = "sys.name")
    private String configKey;


    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", example = "系统名称")
    private String remark;

}
