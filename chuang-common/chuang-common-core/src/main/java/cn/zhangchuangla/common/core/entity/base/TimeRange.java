package cn.zhangchuangla.common.core.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/23 15:38
 */
@Data
public class TimeRange {

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2023-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "开始时间", example = "2023-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime endTime;
}
