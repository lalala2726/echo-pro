package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典项表
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统字典项添加请求对象", description = "系统字典项添加请求对象")
public class SysDictValueAddRequest {

    /**
     * 所属字典键
     */
    @Schema(description = "所属字典键", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictKey;

    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", example = "启用", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", example = "0", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String value;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "1", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;


}
