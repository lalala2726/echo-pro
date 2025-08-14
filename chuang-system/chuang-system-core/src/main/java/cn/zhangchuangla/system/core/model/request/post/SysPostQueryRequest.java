package cn.zhangchuangla.system.core.model.request.post;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.common.core.entity.base.TimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "岗位信息列表请求对象", description = "岗位信息列表请求对象")
public class SysPostQueryRequest extends BasePageRequest {

    /**
     * 岗位ID
     */
    @Schema(name = "岗位ID", example = "1", type = "integer")
    private Long id;

    /**
     * 岗位编码
     */
    @Schema(name = "岗位编码", example = "CT001", type = "string")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称", example = "程序员", type = "string")
    private String postName;

    /**
     * 排序
     */
    @Schema(name = "排序", example = "1", type = "integer")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(name = "状态(0-正常,1-停用)", example = "0", type = "integer")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间", type = "object", description = "创建时间范围", example = "{\"startTime\":\"2023-01-01\",\"endTime\":\"2023-01-01\"}")
    private TimeRange createTime;

}
