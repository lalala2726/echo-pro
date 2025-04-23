package cn.zhangchuangla.system.model.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/23 20:40
 */
@Data
@Schema(description = "分配菜单树视图对象")
public class MenuTreeVo {


    /**
     * 菜单树
     */
    @Schema(description = "菜单ID")
    private List<MenuListVo> menuListVo;

    /**
     * 选中的菜单ID
     */
    @Schema(description = "选择的菜单ID")
    private List<Long> selectedMenuId;
}
