package cn.zhangchuangla.system.model.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单查询对象
 *
 * @author haoxr
 * @since 2022/10/28
 */
@Schema(description = "菜单查询对象")
@Data
public class MenuQueryRequest {

    /**
     * 关键字(菜单名称)
     */
    @Schema(description = "关键字(菜单名称)")
    private String keywords;

    /**
     * 状态(1->显示；0->隐藏)
     */
    @Schema(description = "状态(1->显示；0->隐藏)")
    private Integer status;

}
