package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/9 06:38
 */
@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class MenuOption {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String title;

    /**
     * 菜单图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 子菜单
     */
    @Schema(description = "子菜单")
    private List<MenuOption> children;

}
