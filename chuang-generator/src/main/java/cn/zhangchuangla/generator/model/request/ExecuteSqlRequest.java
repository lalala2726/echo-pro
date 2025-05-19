package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * SQL执行请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "SQL执行请求")
public class ExecuteSqlRequest {

    /**
     * SQL语句
     */
    @NotBlank(message = "SQL语句不能为空")
    @Schema(description = "SQL语句")
    private String sqlContent;
}