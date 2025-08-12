package cn.zhangchuangla.system.core.model.request.dict;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 字典数据查询请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典数据查询请求对象", description = "字典数据查询请求对象")
public class SysDictDataQueryRequest extends BasePageRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 字典标签
     */
    @Schema(description = "字典标签", example = "启用", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictLabel;

    /**
     * 字典值
     */
    @Schema(description = "字典值", example = "0", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictValue;

    /**
     * 状态：1启用，0禁用
     */
    @Schema(description = "状态：1启用，0禁用", example = "1", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2023-01-01", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2023-12-31", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endTime;
}
