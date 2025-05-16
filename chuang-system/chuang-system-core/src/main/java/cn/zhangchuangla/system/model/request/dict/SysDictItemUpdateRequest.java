package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典项表
 * @author zhangchuang
 */
@Data
@Schema(description = "系统字典项修改请求对象")
public class SysDictItemUpdateRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 所属字典类型编码
     */
    @Schema(description = "所属字典类型编码", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictType;

    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String itemLabel;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String itemValue;

    /**
     * 回显方式
     */
    @Schema(description = "回显方式", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tag;

    /**
     * 排序值
     */
    @Schema(description = "排序值", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;


}
