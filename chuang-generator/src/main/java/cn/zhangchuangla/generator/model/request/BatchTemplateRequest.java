package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量设置模板类型请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "批量设置模板类型请求")
public class BatchTemplateRequest {

  @Schema(description = "表ID列表")
  @NotNull(message = "表ID列表不能为空")
  @NotEmpty(message = "表ID列表不能为空")
  private List<Long> tableIds;

  @Schema(description = "模板类型")
  @NotNull(message = "模板类型不能为空")
  private String templateType;
}