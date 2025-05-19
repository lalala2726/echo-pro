package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量生成代码请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "批量生成代码请求")
public class BatchGenCodeRequest {

    /**
     * 表ID列表
     */
    @NotEmpty(message = "表ID不能为空")
    @Schema(description = "表ID列表")
    private List<Long> tableIds;
}