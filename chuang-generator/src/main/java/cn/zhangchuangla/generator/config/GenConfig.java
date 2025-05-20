package cn.zhangchuangla.generator.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基本配置信息
 *
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:54
 */
@Data
@Schema(name = "代码生成配置", description = "代码生成配置,用于配置整体的信息")
public class GenConfig {

    /**
     * 作者
     */
    @Schema(description = "作者", type = "string")
    private String author = "Chuang";

    /**
     * 包名
     */
    @Schema(description = "包名", type = "string")
    private String packageName = "cn.zhangchuangla.system";

    /**
     * 生成路径
     */
    @Schema(description = "生成路径", type = "string")
    private String genPath = "/";
}
