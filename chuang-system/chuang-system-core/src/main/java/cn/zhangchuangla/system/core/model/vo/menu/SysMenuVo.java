package cn.zhangchuangla.system.core.model.vo.menu;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "菜单视图对象", description = "展示菜单详细信息")
public class SysMenuVo extends BaseVo {

    /**
     * ID
     */
    @Schema(description = "编号", type = "number", example = "1")
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称", type = "string", example = "系统管理")
    private String name;

    /**
     * 标题
     */
    @Schema(description = "标题", type = "string", example = "系统管理")
    private String title;

    /**
     * 路径
     */
    @Schema(description = "路径", type = "string", example = "/dashboard")
    private String path;

    /**
     * 类型
     */
    @Schema(description = "类型", type = "string", example = "M")
    private String type;

    /**
     * 状态
     */
    @Schema(description = "状态", type = "string", example = "0")
    private Integer status;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID", type = "number", example = "0")
    private Long parentId;

    /**
     * 激活路径
     */
    @Schema(description = "激活路径", type = "string", example = "/dashboard")
    private String activePath;

    /**
     * 激活图标
     */
    @Schema(description = "激活图标", type = "string", example = "el-icon-setting")
    private String activeIcon;

    /**
     * 图标
     */
    @Schema(description = "图标", type = "string", example = "el-icon-setting")
    private String icon;

    /**
     * 组件
     */
    @Schema(description = "组件", type = "string", example = "views/dashboard/index.vue")
    private String component;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识", type = "string", example = "dashboard")
    private String permission;

    /**
     * 徽标类型
     */
    @Schema(description = "徽标类型", type = "string", example = "normal")
    private String badgeType;

    /**
     * 徽标
     */
    @Schema(description = "徽标", type = "string", example = "1")
    private String badge;

    /**
     * 徽标颜色
     */
    @Schema(description = "徽标颜色", type = "string", example = "default")
    private String badgeVariants;

    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存", type = "number", example = "0")
    private Integer keepAlive;

    /**
     * 是否固定标签页
     */
    @Schema(description = "是否固定标签页", type = "boolean", example = "false")
    private Boolean affixTab;

    /**
     * 隐藏菜单
     */
    @Schema(description = "隐藏菜单", type = "boolean", example = "false")
    private Boolean hideInMenu;

    /**
     * 隐藏子菜单
     */
    @Schema(description = "隐藏子菜单", type = "boolean", example = "false")
    private Boolean hideChildrenInMenu;

    /**
     * 隐藏在面包屑中
     */
    @Schema(description = "隐藏在面包屑中", type = "boolean", example = "false")
    private Boolean hideInBreadcrumb;

    /**
     * 隐藏在标签页中
     */
    @Schema(description = "隐藏在标签页中", type = "boolean", example = "false")
    private Boolean hideInTab;

    /**
     * 外部链接地址
     */
    @Schema(description = "外部链接地址", type = "string", example = "https://www.baidu.com")
    private String link;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "number", example = "0")
    private Integer sort;

}
