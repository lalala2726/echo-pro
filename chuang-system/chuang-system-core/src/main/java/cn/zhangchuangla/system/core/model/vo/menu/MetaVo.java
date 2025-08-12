package cn.zhangchuangla.system.core.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 菜单元数据对象
 *
 * @author Chuang
 */
@Schema(name = "菜单元数据视图对象", description = "菜单元数据视图对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MetaVo {

    /**
     * 激活图标（菜单）
     */
    @Schema(description = "激活图标（菜单）", type = "string", example = "el-icon-setting")
    private String activeIcon;

    /**
     * 当前激活的菜单，有时候不想激活现有菜单，需要激活父级菜单时使用
     */
    @Schema(description = "当前激活的菜单，有时候不想激活现有菜单，需要激活父级菜单时使用", type = "string", example = "/dashboard")
    private String activePath;

    /**
     * 是否固定标签页，默认 false
     */
    @Schema(description = "是否固定标签页，默认 false", type = "boolean", example = "false")
    private Boolean affixTab;

    /**
     * 固定标签页的顺序，默认 0
     */
    @Schema(description = "固定标签页的顺序，默认 0", type = "number", example = "0")
    private Integer affixTabOrder;

    /**
     * 需要特定的角色标识才可以访问，默认 []
     */
    @Schema(description = "需要特定的角色标识才可以访问，默认 []", type = "array", example = "[admin, editor]")
    private Set<String> authority;

    /**
     * 徽标
     */
    @Schema(description = "徽标", type = "string", example = "1")
    private String badge;

    /**
     * 徽标类型，dot 或 normal
     */
    @Schema(description = "徽标类型，dot 或 normal", type = "string", example = "normal")
    private String badgeType;

    /**
     * 徽标颜色
     * 可选: default, destructive, primary, success, warning, 或自定义字符串
     */
    @Schema(description = "徽标颜色", type = "string", example = "default")
    private String badgeVariants;

    /**
     * 路由的完整路径作为key（默认 true）
     */
    @Schema(description = "路由的完整路径作为key（默认 true）", type = "boolean", example = "true")
    private Boolean fullPathKey;

    /**
     * 当前路由的子级在菜单中不展现，默认 false
     */
    @Schema(description = "当前路由的子级在菜单中不展现，默认 false", type = "boolean", example = "false")
    private Boolean hideChildrenInMenu;

    /**
     * 当前路由在面包屑中不展现，默认 false
     */
    @Schema(description = "当前路由在面包屑中不展现，默认 false", type = "boolean", example = "false")
    private Boolean hideInBreadcrumb;

    /**
     * 当前路由在菜单中不展现，默认 false
     */
    @Schema(description = "当前路由在菜单中不展现，默认 false", type = "boolean", example = "false")
    private Boolean hideInMenu;

    /**
     * 当前路由在标签页不展现，默认 false
     */
    @Schema(description = "当前路由在标签页不展现，默认 false", type = "boolean", example = "false")
    private Boolean hideInTab;

    /**
     * 图标（菜单/tab）
     */
    @Schema(description = "图标（菜单/tab）", type = "string", example = "el-icon-setting")
    private String icon;

    /**
     * iframe 地址
     */
    @Schema(description = "iframe 地址", type = "string", example = "https://www.baidu.com")
    private String iframeSrc;

    /**
     * 忽略权限，直接可以访问，默认 false
     */
    @Schema(description = "忽略权限，直接可以访问，默认 false", type = "boolean", example = "false")
    private Boolean ignoreAccess;

    /**
     * 开启KeepAlive缓存
     */
    @Schema(description = "开启KeepAlive缓存", type = "boolean", example = "true")
    private Boolean keepAlive;

    /**
     * 外链-跳转路径
     */
    @Schema(description = "外链-跳转路径", type = "string", example = "https://www.baidu.com")
    private String link;

    /**
     * 路由是否已经加载过
     */
    @Schema(description = "路由是否已经加载过", type = "boolean", example = "true")
    private Boolean loaded;

    /**
     * 标签页最大打开数量
     */
    @Schema(description = "标签页最大打开数量", type = "number", example = "10")
    private Integer maxNumOfOpenTab;

    /**
     * 菜单可以看到，但是访问会被重定向到403
     */
    @Schema(description = "菜单可以看到，但是访问会被重定向到403", type = "boolean", example = "false")
    private Boolean menuVisibleWithForbidden;

    /**
     * 当前路由不使用基础布局（仅在顶级生效）
     */
    @Schema(description = "当前路由不使用基础布局（仅在顶级生效）", type = "boolean", example = "false")
    private Boolean noBasicLayout;

    /**
     * 在新窗口打开
     */
    @Schema(description = "在新窗口打开", type = "boolean", example = "false")
    private Boolean openInNewWindow;

    /**
     * 用于路由->菜单排序
     */
    @Schema(description = "用于路由->菜单排序", type = "number", example = "0")
    private Integer order;

    /**
     * 菜单所携带的参数
     */
    @Schema(description = "菜单所携带的参数", type = "object", example = "{\"id\": 1}")
    private Map<String, Object> query;

    /**
     * 标题名称（必填）
     */
    @Schema(description = "标题名称（必填）", type = "string", example = "系统管理")
    private String title;

}
