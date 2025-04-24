package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 菜单表单对象
 *
 * @author Ray.Hao
 * @since 2024/06/23
 */
@Schema(description = "菜单表单对象")
@Data
public class MenuVo {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String name;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型")
    private Integer type;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String routeName;

    /**
     * 路由路径
     */
    @Schema(description = "路由路径")
    private String routePath;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 菜单排序
     */
    @Schema(description = "菜单排序(数字越小排名越靠前)")
    private Integer sort;

    /**
     * 菜单是否可见
     */
    @Schema(description = "菜单是否可见(1:显示;0:隐藏)")
    private Integer visible;

    /**
     * 菜单图标
     */
    @Schema(description = "ICON")
    private String icon;

    /**
     * 跳转路径
     */
    @Schema(description = "跳转路径")
    private String redirect;

    /**
     * 父节点路径
     */
    @Schema(description = "按钮权限标识")
    private String permission;

    /**
     * 路由参数
     */
    @Schema(description = "子菜单")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<MenuVo> children;
}
