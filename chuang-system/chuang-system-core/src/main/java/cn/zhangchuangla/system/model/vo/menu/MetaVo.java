package cn.zhangchuangla.system.model.vo.menu;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路由元数据信息
 *
 * @author zhangchuang
 */
@Data
@NoArgsConstructor
public class MetaVo {

    /**
     * 菜单标题，设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 菜单图标，设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 是否缓存，设置为true，则不会被 <keep-alive>缓存
     */
    private boolean noCache;

    /**
     * 链接地址（http(s)://开头）
     */
    private String link;


}
