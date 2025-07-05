package cn.zhangchuangla.system.model.vo.menu;

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
 * @since 2024/04/12
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
    private String activeIcon;

    /**
     * 当前激活的菜单，有时候不想激活现有菜单，需要激活父级菜单时使用
     */
    private String activePath;

    /**
     * 是否固定标签页，默认 false
     */
    private Boolean affixTab;

    /**
     * 固定标签页的顺序，默认 0
     */
    private Integer affixTabOrder;

    /**
     * 需要特定的角色标识才可以访问，默认 []
     */
    private Set<String> authority;

    /**
     * 徽标
     */
    private String badge;

    /**
     * 徽标类型，dot 或 normal
     */
    private String badgeType;

    /**
     * 徽标颜色
     * 可选: default, destructive, primary, success, warning, 或自定义字符串
     */
    private String badgeVariants;

    /**
     * 路由的完整路径作为key（默认 true）
     */
    private Boolean fullPathKey;

    /**
     * 当前路由的子级在菜单中不展现，默认 false
     */
    private Boolean hideChildrenInMenu;

    /**
     * 当前路由在面包屑中不展现，默认 false
     */
    private Boolean hideInBreadcrumb;

    /**
     * 当前路由在菜单中不展现，默认 false
     */
    private Boolean hideInMenu;

    /**
     * 当前路由在标签页不展现，默认 false
     */
    private Boolean hideInTab;

    /**
     * 图标（菜单/tab）
     */
    private String icon;

    /**
     * iframe 地址
     */
    private String iframeSrc;

    /**
     * 忽略权限，直接可以访问，默认 false
     */
    private Boolean ignoreAccess;

    /**
     * 开启KeepAlive缓存
     */
    private Boolean keepAlive;

    /**
     * 外链-跳转路径
     */
    private String link;

    /**
     * 路由是否已经加载过
     */
    private Boolean loaded;

    /**
     * 标签页最大打开数量
     */
    private Integer maxNumOfOpenTab;

    /**
     * 菜单可以看到，但是访问会被重定向到403
     */
    private Boolean menuVisibleWithForbidden;

    /**
     * 当前路由不使用基础布局（仅在顶级生效）
     */
    private Boolean noBasicLayout;

    /**
     * 在新窗口打开
     */
    private Boolean openInNewWindow;

    /**
     * 用于路由->菜单排序
     */
    private Integer order;

    /**
     * 菜单所携带的参数
     */
    private Map<String, Object> query;

    /**
     * 标题名称（必填）
     */
    private String title;

}
