package cn.zhangchuangla.system.core.model.vo.dept;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 部门视图
 *
 * @author Chuang
 */
@Data
@Schema(name = "部门视图对象", description = "部门视图对象")
public class SysDeptVo {

    /**
     * 部门ID
     */
    @Schema(name = "部门ID", description = "部门唯一标识符", type = "integer", example = "1")
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(name = "部门名称", description = "部门的名称", type = "string", example = "研发部")
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(name = "父部门ID", description = "上级部门的唯一标识符", type = "integer", example = "0")
    private Long parentId;

    /**
     * 部门负责人
     */
    @Schema(name = "部门负责人", description = "负责该部门的人员姓名", type = "string", example = "张三")
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", type = "string", example = "负责产品研发与技术创新")
    private String description;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）", type = "integer", example = "0")
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间", type = "string", example = "2023-01-01 12:00:00")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", type = "string", example = "2023-01-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "新增者", type = "string", example = "系统管理员")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新者", type = "string", example = "系统管理员")
    private String updateBy;

}
