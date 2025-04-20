package cn.zhangchuangla.common.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 键值对
 */
@Schema(description = "键值对")
@Data
@NoArgsConstructor
public class KeyValue {

    /**
     * 选择的值
     */
    @Schema(description = "选项的值")
    private String key;

    /**
     * 选择的标签
     */
    @Schema(description = "选项的标签")
    private String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
