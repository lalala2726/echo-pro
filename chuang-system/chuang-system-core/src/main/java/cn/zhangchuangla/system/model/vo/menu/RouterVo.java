package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 路由配置信息
 *
 * @author zhangchuang
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 路由属性
     */
    private MetaVo meta;

    /**
     * 是否隐藏路由（0否1是）
     */
    private Boolean hidden;

    /**
     * 是否总是显示
     */
    private Boolean alwaysShow;

    /**
     * 子路由
     */
    private List<RouterVo> children;
}
