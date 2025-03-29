package cn.zhangchuangla.system.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路由显示信息
 * 
 * @author zhangchuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "路由元数据信息")
public class MetaVo {
    
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    @Schema(description = "路由标题")
    private String title;

    /**
     * 设置该路由的图标
     */
    @Schema(description = "路由图标")
    private String icon;
    
    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存", defaultValue = "false")
    private boolean noCache;
    
    /**
     * 是否固定在标签栏
     */
    @Schema(description = "是否固定在标签栏", defaultValue = "false")
    private boolean affix;
    
    /**
     * 是否总是显示
     */
    @Schema(description = "是否总是显示", defaultValue = "false")
    private boolean alwaysShow;
}
