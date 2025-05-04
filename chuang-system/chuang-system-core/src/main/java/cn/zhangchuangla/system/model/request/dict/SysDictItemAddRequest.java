package cn.zhangchuangla.system.model.request.dict;

import cn.zhangchuangla.common.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典项表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "系统字典项添加请求对象")
public class SysDictItemAddRequest extends BaseVo {


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
