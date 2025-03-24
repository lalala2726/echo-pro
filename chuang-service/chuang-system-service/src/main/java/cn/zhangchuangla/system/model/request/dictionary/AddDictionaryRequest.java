package cn.zhangchuangla.system.model.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author Chuang
 * <p>
 * created on 2025/3/2 23:17
 */
@Schema(name = "添加字典请求", description = "添加字典请求参数")
@Data
public class AddDictionaryRequest {

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    private String name;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
}
