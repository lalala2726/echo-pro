package cn.zhangchuangla.system.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 角色权限对应关系实体类
 *
 * @author Chuang
 */
@TableName(value = "sys_role_permissions")
@Data
@Schema(name = "角色权限对应关系实体类")
public class SysRolePermissions {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long rolePermissionId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 权限id
     */
    private Long permissionId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;
}
