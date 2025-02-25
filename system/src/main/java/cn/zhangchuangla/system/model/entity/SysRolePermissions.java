package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 角色权限对应关系实体类
 */
@TableName(value = "sys_role_permissions")
@Data
@Schema(name = "角色权限对应关系实体类")
public class SysRolePermissions {

    /**
     * 主键
     */
    @TableId
    @Schema(description = "主键")
    private Long rolePermissionId;

    /**
     * 角色id
     */
    @Schema(description = "角色ID")
    private Long roleId;

    /**
     * 权限id
     */
    @Schema(description = "权限ID")
    private Long permissionId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
