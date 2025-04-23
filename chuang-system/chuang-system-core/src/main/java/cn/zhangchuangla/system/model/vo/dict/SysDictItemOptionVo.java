package cn.zhangchuangla.system.model.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典项表
 */
@Data
@Schema(description = "字典项视图对象")
public class SysDictItemOptionVo {

    /**
     * 关联字典编码
     */
    @Schema(description = "关联字典编码")
    private String dictCode;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值")
    private String value;

    /**
     * 字典项标签
     */
    @Schema(description = "字典项标签")
    private String label;

    /**
     * 标签类型
     */
    @Schema(description = "标签类型")
    private String tagType;


}
