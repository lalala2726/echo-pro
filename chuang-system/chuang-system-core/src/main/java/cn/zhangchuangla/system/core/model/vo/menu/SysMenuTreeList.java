package cn.zhangchuangla.system.core.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/12 13:48
 */
@Data
@Schema(name = "菜单角色权限视图对象", description = "菜单角色权限对象")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SysMenuTreeList {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", type = "number", example = "1")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", type = "string", example = "系统管理")
    private String title;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID", type = "number", example = "0")
    private Long parentId;

    /**
     * 图标
     */
    @Schema(description = "图标", type = "string", example = "el-icon-setting")
    private String icon;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型", type = "string", example = "M")
    private String type;

    /**
     * 子菜单
     */
    @Schema(description = "子菜单", type = "array")
    private List<SysMenuTreeList> children;
}
