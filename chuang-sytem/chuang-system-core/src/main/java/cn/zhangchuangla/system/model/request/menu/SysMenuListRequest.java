package cn.zhangchuangla.system.model.request.menu;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "菜单权限请求类")
public class SysMenuListRequest extends BasePageRequest {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long menuId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String menuName;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long parentId;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer orderNum;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String component;

    /**
     * 是否外链（0是 1否）
     */
    @Schema(description = "是否外链（0是 1否）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Schema(description = "菜单状态（0显示 1隐藏）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String perms;


}