package cn.zhangchuangla.generator.model.request;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表列表查询请求
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "表列表查询请求")
public class GenTableListRequest extends BasePageRequest {

    /**
     * 表名称
     */
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 表描述
     */
    @Schema(description = "表描述")
    private String tableComment;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String beginTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;
}