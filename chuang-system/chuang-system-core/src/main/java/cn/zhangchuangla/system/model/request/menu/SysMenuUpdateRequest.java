package cn.zhangchuangla.system.model.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单更新请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "菜单更新请求")
public class SysMenuUpdateRequest {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", type = "string", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单ID不能为空")
    @Min(value = 0, message = "菜单ID不能小于0")
    private Long menuId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID", type = "string", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long parentId;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序", type = "string", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer orderNum;

    /**
     * 是否外部跳转（0否 1是）
     */
    @Schema(description = "是否外部跳转（0否 1是）", type = "string", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer externalLink;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull(message = "路由地址不能为空")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String component;

    /**
     * 路由参数
     */
    @Schema(description = "路由参数", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String query;


    /**
     * 是否为外链（0是 1否）
     */
    @Schema(description = "是否为外链（0是 1否）", type = "string", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）", type = "string", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Schema(description = "菜单状态（0显示 1隐藏）", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）", type = "string", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String permission;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String icon;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "string", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;
}
