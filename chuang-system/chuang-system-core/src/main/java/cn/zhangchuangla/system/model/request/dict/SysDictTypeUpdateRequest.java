package cn.zhangchuangla.system.model.request.dict;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统字典修改请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统字典修改请求对象", description = "系统字典修改请求对象")
public class SysDictTypeUpdateRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", example = "sys_user_sex", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "用户性别", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "男/女", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
