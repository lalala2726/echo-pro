package cn.zhangchuangla.system.model.request.dept;

import cn.zhangchuangla.common.base.BasePageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门列表请求类
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "部门列表请求类", description = "部门列表请求类")
public class SysDeptListRequest extends BasePageRequest {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", type = "integer", example = "1")
    private Integer deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", type = "string", example = "研发部")
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", type = "integer", format = "int64", example = "100")
    private Long parentId;

    /**
     * 部门创建时间
     */
    @Schema(description = "部门状态", type = "integer", format = "int32", example = "1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "integer", format = "int64", example = "200")
    private String manager;

}
