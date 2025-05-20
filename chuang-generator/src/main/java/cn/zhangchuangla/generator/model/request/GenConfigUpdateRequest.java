package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 基本配置修改对象
 *
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:54
 */
@Data
@Schema(name = "基本配置修改对象", description = "基本配置修改对象")
public class GenConfigUpdateRequest {

    /**
     * 作者
     */
    @Schema(description = "作者", example = "Chuang")
    @NotNull(message = "作者不能为空")
    private String author = "Chuang";

    /**
     * 包名
     */
    @Schema(description = "包名", example = "cn.zhangchuangla.system")
    @NotNull(message = "包名不能为空")
    private String packageName = "cn.zhangchuangla.system";
}
