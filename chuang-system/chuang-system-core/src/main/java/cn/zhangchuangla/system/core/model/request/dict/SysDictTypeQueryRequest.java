package cn.zhangchuangla.system.core.model.request.dict;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 字典类型查询请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典类型查询请求对象", description = "字典类型查询请求对象")
public class SysDictTypeQueryRequest extends BasePageRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "用户状态", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
