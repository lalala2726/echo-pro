package cn.zhangchuangla.quartz.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务组查询请求
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务组查询请求")
public class SysJobGroupQueryRequest extends BasePageRequest {

    /**
     * 任务组名称
     */
    @Schema(description = "任务组名称")
    private String groupName;

    /**
     * 任务组编码
     */
    @Schema(description = "任务组编码")
    private String groupCode;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
}
