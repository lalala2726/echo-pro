package cn.zhangchuangla.system.core.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色和菜单关联表
 *
 * @author Chuang
 */
@TableName(value = "sys_role_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysRoleMenu {

    /**
     * 角色ID
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;
}
