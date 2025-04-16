package cn.zhangchuangla.common.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 键值对
 *
 * @author haoxr
 * @since 2024/5/25
 */
@Schema(description = "键值对")
@Data
@NoArgsConstructor
public class KeyValue {

    @Schema(description = "选项的值")
    private String key;
    @Schema(description = "选项的标签")
    private String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
