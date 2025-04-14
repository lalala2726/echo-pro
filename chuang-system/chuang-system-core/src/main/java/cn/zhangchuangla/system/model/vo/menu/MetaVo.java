package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由元数据
 *
 * @author zhangchuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MetaVo {

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private Boolean keepAlive;

    /**
     * 排序编号
     */
    private Integer rank;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 按钮权限列表
     */
    private List<String> auths;

    /**
     * 激活菜单
     */
    private String activeMenu;

}
