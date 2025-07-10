package cn.zhangchuangla.common.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/19 12:29
 */
@Schema(description = "下拉选项对象")
@Data
@NoArgsConstructor
public class Option<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -2430627728459706234L;

    @Schema(description = "选项的值")
    private T value;

    @Schema(description = "选项的标签")
    private String label;

    @Schema(description = "标签类型")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String tag;

    @Schema(description = "子选项列表")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<Option<T>> children;

    @Schema(description = "是否禁用")
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean disabled;


    public Option(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public Option(T value, String label, List<Option<T>> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    public Option(T value, String label, String tag) {
        this.value = value;
        this.label = label;
        this.tag = tag;
    }
}
