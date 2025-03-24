package cn.zhangchuangla.system.model.request.dictionary;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * 字典表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典请求类", description = "请求字典数据进行指定参数查询")
public class DictionaryRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1L, message = "主键不能小于1")
    private Long id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String description;
}
