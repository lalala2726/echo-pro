package cn.zhangchuangla.system.model.request.post;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * 岗位表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "岗位信息列表请求对象", description = "岗位信息列表请求对象")
public class SysPostQueryRequest extends BasePageRequest {

    /**
     * 岗位ID
     */
    @Schema(name = "岗位ID", example = "1", type = "integer")
    @Min(value = 1, message = "岗位ID不能小于1")
    private Integer postId;

    /**
     * 岗位编码
     */
    @Schema(name = "岗位编码", example = "CT001", type = "string")
    @Size(max = 64, min = 1, message = "岗位编码长度在1-64个字符")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称", example = "程序员", type = "string")
    @Size(max = 100, min = 1, message = "岗位名称长度在1-100个字符")
    private String postName;

    /**
     * 排序
     */
    @Schema(name = "排序", example = "1", type = "integer")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(name = "状态(0-正常,1-停用)", example = "0", type = "integer")
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

}
