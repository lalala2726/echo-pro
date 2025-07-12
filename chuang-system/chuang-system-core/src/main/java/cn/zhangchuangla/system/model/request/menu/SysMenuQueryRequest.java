package cn.zhangchuangla.system.model.request.menu;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单列表查询请求对象
 *
 * @author Chuang
 * <p>
 * created on 2025/5/14 10:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统菜单列表查询请求对象", description = "系统菜单列表查询请求对象")
public class SysMenuQueryRequest extends BasePageRequest {

    /**
     * 名称
     */
    @Schema(description = "名称", type = "string", example = "System")
    private String name;

    /**
     * 路径
     */
    @Schema(description = "路径", type = "string", example = "/system")
    private String path;

    /**
     * 类型
     */
    @Schema(description = "类型", type = "string", example = "catalog")
    private String type;

}
