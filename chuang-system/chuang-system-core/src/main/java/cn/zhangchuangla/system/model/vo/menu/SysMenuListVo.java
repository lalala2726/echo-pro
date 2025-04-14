package cn.zhangchuangla.system.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 菜单权限表
 */
@Data
@Schema(description = "菜单权限列表视图类")
public class SysMenuListVo {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "重定向地址")
    private String redirect;

    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @Schema(description = "是否隐藏（0显示 1隐藏）")
    private Integer hidden;

    @Schema(description = "是否外链（0是 1否）")
    private String isFrame;

    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private String isCache;

    @Schema(description = "菜单标题")
    private String title;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序编号")
    private Integer rank;

    @Schema(description = "角色列表")
    private String roles;

    @Schema(description = "按钮权限列表")
    private String auths;

    @Schema(description = "是否总是显示")
    private Integer alwaysShow;

    @Schema(description = "激活菜单")
    private String activeMenu;

    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "子菜单")
    private List<SysMenuListVo> children;
}
