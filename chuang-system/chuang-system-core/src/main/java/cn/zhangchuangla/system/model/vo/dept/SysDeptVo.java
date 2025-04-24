package cn.zhangchuangla.system.model.vo.dept;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 部门视图
 */
@Data
public class SysDeptVo {

    /**
     * 部门ID
     */
    @Schema(name = "部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(name = "部门名称")
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(name = "父部门ID")
    private Long parentId;

    /**
     * 部门负责人
     */
    @Schema(name = "部门负责人")
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述")
    private String description;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "新增者")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新者")
    private String updateBy;

}
