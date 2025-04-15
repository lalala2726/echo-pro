package cn.zhangchuangla.system.model.request.menu;

import cn.zhangchuangla.common.model.entity.KeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 菜单表单对象
 *
 * @author Ray.Hao
 * @since 2024/06/23
 */
@Schema(description = "菜单表单对象")
@Data
public class MenuAddRequest {


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
     * 菜单类型（1-菜单 2-目录 3-外链 4-按钮）
     */
    @Schema(description = "菜单类型（1-菜单 2-目录 3-外链 4-按钮）")
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
     * 组件路径(vue页面完整路径，省略.vue后缀)
     */
    @Schema(description = "组件路径(vue页面完整路径，省略.vue后缀)")
    private String component;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String perm;

    /**
     * 显示状态(1:显示;0:隐藏)
     */
    @Schema(description = "显示状态(1:显示;0:隐藏)")
    @Range(max = 1, min = 0, message = "显示状态不正确")
    private Integer visible;

    /**
     * 排序(数字越小排名越靠前)
     */
    @Schema(description = "排序(数字越小排名越靠前)")
    private Integer sort;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 跳转路径
     */
    @Schema(description = "跳转路径")
    private String redirect;

    /**
     * 【菜单】是否开启页面缓存(1:开启;0:关闭)
     */
    @Schema(description = "【菜单】是否开启页面缓存", example = "1")
    private Integer keepAlive;

    /**
     * 【目录】只有一个子路由是否始终显示
     */
    @Schema(description = "【目录】只有一个子路由是否始终显示", example = "1")
    private Integer alwaysShow;

    /**
     * 路由参数
     */
    @Schema(description = "路由参数")
    private List<KeyValue> params;
}
