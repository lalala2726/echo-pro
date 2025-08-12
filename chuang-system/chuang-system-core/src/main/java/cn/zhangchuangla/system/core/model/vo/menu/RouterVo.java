package cn.zhangchuangla.system.core.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由对象
 *
 * @author Chuang
 * <p>
 * created on 2025年4月29日 22:14:26
 */
@Schema(name = "路由视图对象", description = "用于前端界面的动态路由")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {

    /**
     * 路由名字
     */
    @Schema(description = "路由名字", type = "string", example = "dashboard")
    private String name;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址", type = "string", example = "/dashboard")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径", type = "string", example = "views/dashboard/index.vue")
    private String component;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型", type = "string", example = "M")
    private String type;


    /**
     * 路由元数据
     */
    @Schema(description = "路由元数据", type = "object")
    private MetaVo meta;

    /**
     * 子路由
     */
    @Schema(description = "子路由", type = "array")
    private List<RouterVo> children;

}
