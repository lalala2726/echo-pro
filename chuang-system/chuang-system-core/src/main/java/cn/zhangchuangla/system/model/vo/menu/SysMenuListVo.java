package cn.zhangchuangla.system.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 菜单实体
 *
 * @author Chuang
 * Created 2025/5/11 20:25
 */
@Data
@Schema(name = "菜单列表视图对象", description = "菜单列表视图对象")
public class SysMenuListVo {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String title;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer orderNum;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String routeName;

    /**
     * 是否为外链（0是 1否）
     */
    @Schema(description = "是否为外链（0是 1否）")
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String type;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）")
    private Integer status;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 徽标
     */
    @Schema(description = "徽标")
    private String badge;

    /**
     * 徽标类型，dot 或 normal
     */
    @Schema(description = "徽标类型，dot 或 normal")
    private String badgeType;

    /**
     * 徽标颜色
     * 可选: default, destructive, primary, success, warning, 或自定义字符串
     */
    @Schema(description = "徽标颜色")
    private String badgeVariants;


    /**
     * 子菜单
     */
    @Schema(description = "子菜单")
    private List<SysMenuListVo> children;

}
