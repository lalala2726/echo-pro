package cn.zhangchuangla.system.model.request.dict;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典项列表请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统字典项列表请求对象", description = "系统字典项列表请求对象")
public class SysDictItemListRequest extends BasePageRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;


    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", example = "男", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String itemLabel;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", example = "0", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String itemValue;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "1", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;

}
