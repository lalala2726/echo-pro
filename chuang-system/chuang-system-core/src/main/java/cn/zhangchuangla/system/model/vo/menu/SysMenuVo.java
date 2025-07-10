package cn.zhangchuangla.system.model.vo.menu;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
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
@Schema(name = "菜单视图对象", description = "展示菜单详细信息")
public class SysMenuVo extends BaseVo {

    /**
     * ID
     */
    @Schema(description = "编号")
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 路径
     */
    @Schema(description = "路径")
    private String path;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID")
    private Long parentId;

    /**
     * 激活路径
     */
    @Schema(description = "激活路径")
    private String activePath;

    /**
     * 激活图标
     */
    @Schema(description = "激活图标")
    private String activeIcon;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 组件
     */
    @Schema(description = "组件")
    private String component;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String permission;

    /**
     * 徽标类型
     */
    @Schema(description = "徽标类型")
    private String badgeType;

    /**
     * 徽标
     */
    @Schema(description = "徽标")
    private String badge;

    /**
     * 徽标颜色
     */
    @Schema(description = "徽标颜色")
    private String badgeVariants;

    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存")
    private Integer keepAlive;

    /**
     * 是否固定标签页
     */
    @Schema(description = "是否固定标签页")
    private Boolean affixTab;

    /**
     * 隐藏菜单
     */
    @Schema(description = "隐藏菜单")
    private Boolean hideInMenu;

    /**
     * 隐藏子菜单
     */
    @Schema(description = "隐藏子菜单")
    private Boolean hideChildrenInMenu;

    /**
     * 隐藏在面包屑中
     */
    @Schema(description = "隐藏在面包屑中")
    private Boolean hideInBreadcrumb;

    /**
     * 隐藏在标签页中
     */
    @Schema(description = "隐藏在标签页中")
    private Boolean hideInTab;

    /**
     * 外部链接地址
     */
    @Schema(description = "外部链接地址")
    private String link;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

}
