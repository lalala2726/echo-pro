package cn.zhangchuangla.system.model.request.dept;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
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
public class SysDeptQueryRequest extends BasePageRequest {

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", type = "string", example = "研发部")
    private String deptName;

    /**
     * 部门状态
     */
    @Schema(description = "部门状态", type = "integer", format = "int", example = "1")
    private Integer status;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "integer", format = "int64", example = "200")
    private String manager;

}
