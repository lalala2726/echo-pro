package cn.zhangchuangla.system.model.request.department;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 部门表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "部门列表请求类")
public class SysDepartmentListRequest extends BasePageRequest {

    /**
     * 部门ID
     */
    @Schema(name = "部门ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer id;

    /**
     * 部门名称
     */
    @Schema(name = "部门名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    /**
     * 父部门ID
     */
    @Schema(name = "父部门ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer parentId;

    /**
     * 部门负责人
     */
    @Schema(name = "部门负责人", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long managerId;

    /**
     * 部门描述
     */
    @Schema(name = "部门描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date create_time;

    /**
     * 更新时间
     */
    @Schema(name = "更新时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date update_time;

    /**
     * 创建人
     */
    @Schema(name = "创建人", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String create_by;

    /**
     * 修改人
     */
    @Schema(name = "修改人", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String update_by;

    /**
     * 备注
     */
    @Schema(name = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
