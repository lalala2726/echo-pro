package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 菜单路由视图对象
 *
 * @author haoxr
 * @since 2020/11/28
 */
@Schema(description = "路由对象")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteVo {

    /**
     * 路由ID
     */
    @Schema(description = "路由路径", example = "user")
    private String path;

    /**
     * 路由组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由重定向路径
     */
    @Schema(description = "跳转链接")
    private String redirect;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String name;

    /**
     * 路由参数
     */
    @Schema(description = "路由属性")
    private Meta meta;

    /**
     * 路由参数
     */
    @Schema(description = "子路由列表")
    private List<RouteVo> children;

    /**
     * 路由参数
     */
    @Schema(description = "路由属性类型")
    @Data
    public static class Meta {

        /**
         * 路由标题
         */
        @Schema(description = "路由title")
        private String title;

        /**
         * 路由图标
         */
        @Schema(description = "ICON")
        private String icon;

        /**
         * 路由权限标识
         */
        @Schema(description = "是否隐藏(true-是 false-否)", example = "true")
        private Boolean hidden;

        /**
         * 路由权限标识
         */
        @Schema(description = "【菜单】是否开启页面缓存", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean keepAlive;

        /**
         * 路由权限标识
         */
        @Schema(description = "【目录】只有一个子路由是否始终显示", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean alwaysShow;

        /**
         * 路由权限标识
         */
        @Schema(description = "路由参数")
        private Map<String, String> params;
    }
}
