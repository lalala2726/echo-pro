package cn.zhangchuangla.system.model.request.post;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 岗位表
 *
 * @author Chuang
 */
@Data
@Schema(name = "添加岗位请求对象", description = "添加岗位请求对象")
public class SysPostAddRequest {

    /**
     * 岗位编码
     */
    @Schema(description = "岗位编码", example = "1001", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "岗位编码必须大于等于1")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", example = "开发工程师", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1", type = "integer")
    @Range(min = 0, max = 999, message = "排序必须在0到999之间")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(description = "状态(0-正常,1-停用)", example = "0", type = "integer")
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;


}
