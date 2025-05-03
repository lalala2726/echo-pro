package cn.zhangchuangla.system.model.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单查询请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "菜单查询请求")
public class MenuQueryRequest {

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;
}