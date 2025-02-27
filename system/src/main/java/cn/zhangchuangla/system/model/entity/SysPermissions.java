package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_permissions")
@Data
@Schema(name = "权限实体类", description = "权限表")
public class SysPermissions extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long permissionId;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String permissionsName;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String permissionsKey;
}
