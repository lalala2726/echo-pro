package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单路由视图对象
 *
 * @author haoxr
 * @since 2020/11/28
 */
@Schema(description = "路由对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteVo {

    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    private MetaVo meta;

    /**
     * 子路由
     */
    private List<RouteVo> children;

    /**
     * 构造函数（用于创建带有完整参数的路由对象）
     */
    public RouteVo(String name, String path, boolean hidden, String redirect, String component, String query, Boolean alwaysShow, MetaVo meta) {
        this.name = name;
        this.path = path;
        this.hidden = hidden;
        this.redirect = redirect;
        this.component = component;
        this.query = query;
        this.alwaysShow = alwaysShow;
        this.meta = meta;
    }
}
