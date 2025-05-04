package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典类型表
 */
@Data
@Schema(description = "系统字典修改请求对象")
public class SysDictTypeUpdateRequest   {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "system_common_status")
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "系统状态")
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "0")
    private String status;

}