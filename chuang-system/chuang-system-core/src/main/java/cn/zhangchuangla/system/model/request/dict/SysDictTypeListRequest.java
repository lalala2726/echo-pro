package cn.zhangchuangla.system.model.request.dict;

import cn.zhangchuangla.common.base.BasePageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典列表请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统字典列表请求对象", description = "系统字典列表请求对象")
public class SysDictTypeListRequest extends BasePageRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "用户状态", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

}
