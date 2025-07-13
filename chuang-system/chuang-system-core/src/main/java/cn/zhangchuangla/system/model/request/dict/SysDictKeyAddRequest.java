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
public class SysDictKeyAddRequest {

    /**
     * 字典编码
     */
    @Schema(description = "字典编码", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictKey;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "用户状态", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

}
