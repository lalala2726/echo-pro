package cn.zhangchuangla.system.model.vo.dict;

import cn.zhangchuangla.common.core.core.entity.base.BaseVo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典项表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统字典项视图对象", description = "系统字典项视图对象")
public class SysDictItemVo extends BaseVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64")
    private Long id;

    /**
     * 所属字典类型编码
     */
    @Schema(description = "所属字典类型编码", type = "string")
    private String dictType;

    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", type = "string")
    private String itemLabel;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", type = "string")
    private String itemValue;

    /**
     * 排序值
     */
    @Schema(description = "排序值", type = "integer")
    private Integer sort;

    /**
     * 回显方式
     */
    @Schema(description = "回显方式", type = "string")
    private String tag;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "integer")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;


}
