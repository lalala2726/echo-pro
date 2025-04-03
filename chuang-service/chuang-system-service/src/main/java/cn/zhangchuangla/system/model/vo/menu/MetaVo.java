package cn.zhangchuangla.system.model.vo.menu;

import cn.zhangchuangla.common.config.jackson.CustomBooleanSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 路由显示信息
 *
 * @author zhangchuang
 */
@Data
@Builder
@Schema(description = "路由元数据信息")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
    @JsonSerialize(using = CustomBooleanSerializer.class)
    private boolean noCache;

    /**
     * 是否固定在标签栏
     */
    @Schema(description = "是否固定在标签栏", defaultValue = "false")
    @JsonSerialize(using = CustomBooleanSerializer.class)
    private boolean affix;

    /**
     * 是否总是显示
     */
    @Schema(description = "是否总是显示", defaultValue = "false")
    @JsonSerialize(using = CustomBooleanSerializer.class)
    private boolean alwaysShow;
}
