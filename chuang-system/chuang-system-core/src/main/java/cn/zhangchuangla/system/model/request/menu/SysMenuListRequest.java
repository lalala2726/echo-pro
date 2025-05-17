package cn.zhangchuangla.system.model.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统菜单列表查询请求对象
 *
 * @author Chuang
 * <p>
 * created on 2025/5/14 10:47
 */
@Data
@Schema(name = "系统菜单列表查询请求对象", description = "系统菜单列表查询请求对象")
public class SysMenuListRequest {

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "用户管理", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String menuName;

    /**
     * 菜单类型
     */
    @Schema(description = "菜单类型", example = "目录/菜单/按钮", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String menuType;

    /**
     * 显示状态
     */
    @Schema(description = "显示状态", example = "0-隐藏 1-显示", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;

    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存", example = "true-是 false-否", type = "boolean", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isCache;

    /**
     * 是否外链
     */
    @Schema(description = "是否外链", example = "true-是 false-否", type = "boolean", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isFrame;

}
