package cn.zhangchuangla.system.model.vo.menu;

import cn.zhangchuangla.common.base.BaseVo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体
 *
 * @author Chuang
 * @since 2025/5/14 13:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuVo extends BaseVo {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long menuId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer orderNum;

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
     * 路由参数
     */
    @Schema(description = "路由参数")
    private String query;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String routeName;

    /**
     * 是否为外链（0是 1否）
     */
    @Schema(description = "是否为外链（0是 1否）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String permission;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

}
