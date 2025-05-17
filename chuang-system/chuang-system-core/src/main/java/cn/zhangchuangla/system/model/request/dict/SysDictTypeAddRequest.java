package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典添加请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统字典添加请求对象", description = "系统字典添加请求对象")
public class SysDictTypeAddRequest {


    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

}
