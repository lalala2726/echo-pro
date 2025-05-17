package cn.zhangchuangla.system.model.request.dept;

import cn.zhangchuangla.common.base.BasePageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

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
    @Schema(description = "部门ID", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long parentId;

    /**
     * 部门创建时间
     */
    @Schema(description = "部门状态", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date create_time;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", type = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date update_time;

    /**
     * 创建人
     */
    @Schema(description = "创建人", type = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String create_by;

    /**
     * 修改人
     */
    @Schema(description = "修改人", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String update_by;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
