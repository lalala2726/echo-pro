package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典项修改请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统字典项修改请求对象", description = "系统字典项修改请求对象")
public class SysDictValueUpdateRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 所属字典类型编码
     */
    @Schema(description = "所属字典类型编码", example = "sys_user_sex", type = "string")
    private String dictKey;

    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", example = "男", type = "string")
    private String label;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", example = "0", type = "string")
    private String value;

    /**
     * 回显方式
     */
    @Schema(description = "回显方式", example = "default", type = "string")
    private String tag;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "1", type = "integer", format = "int32")
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "string")
    private String status;


}
