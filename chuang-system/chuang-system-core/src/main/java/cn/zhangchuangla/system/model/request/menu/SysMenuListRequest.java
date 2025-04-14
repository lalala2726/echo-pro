package cn.zhangchuangla.system.model.request.menu;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单列表查询请求
 *
 * @author zhangchuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单列表查询请求")
public class SysMenuListRequest extends BasePageRequest {

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;
}
