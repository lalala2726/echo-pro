package cn.zhangchuangla.system.model.vo.permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/21 21:05
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuListVo {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long menuId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型")
    private Integer menuType;

    /**
     * 子菜单
     */
    @Schema(description = "子菜单")
    private List<MenuListVo> children;


}
