package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    @Schema(description = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    @Schema(description = "设置该路由的图标，对应路径src/assets/icons/svg")
    private String icon;

    /**
     * 是否在菜单中显示
     */
    @Schema(description = "是否在菜单中显示")
    private Boolean showLink;


    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    @Schema(description = "设置为true，则不会被 <keep-alive>缓存")
    private Boolean noCache;

    /**
     * 内链地址（http(s)://开头）
     */
    @Schema(description = "内链地址（http(s)://开头）")
    private String link;

    /**
     * 菜单名称右侧的额外图标
     */
    @Schema(description = "菜单名称右侧的额外图标")
    private String extraIcon;

    /**
     * 是否显示父级菜单
     */
    @Schema(description = "是否显示父级菜单")
    private Boolean showParent;

    /**
     * 按钮级别权限设置
     */
    @Schema(description = "按钮级别权限设置")
    private String[] auths;

    /**
     * 是否缓存该路由页面
     */
    @Schema(description = "是否缓存该路由页面")
    private Boolean keepAlive;

    /**
     * 需要内嵌的iframe链接地址
     */
    @Schema(description = "需要内嵌的iframe链接地址")
    private String frameSrc;

    /**
     * 内嵌的iframe页面是否开启首次加载动画
     */
    @Schema(description = "内嵌的iframe页面是否开启首次加载动画")
    private Boolean frameLoading;

    /**
     * 当前菜单名称或自定义信息禁止添加到标签页
     */
    @Schema(description = "当前菜单名称或自定义信息禁止添加到标签页")
    private Boolean hiddenTag;

    /**
     * 将某个菜单激活
     */
    @Schema(description = "将某个菜单激活")
    private String activePath;

    /**
     * 角色权限
     */
    @Schema(description = "角色权限")
    private Set<String> roles;

    /**
     * 显示在标签页的最大数量
     */
    @Schema(description = "显示在标签页的最大数量")
    private Integer dynamicLevel;


}
