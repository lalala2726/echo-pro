package cn.zhangchuangla.system.model.vo.dict;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统字典类型表
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统字典类型视图对象", description = "系统字典列表视图对象")
public class SysDictKeyVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64")
    private Long id;

    /**
     * 字典键
     */
    @Schema(description = "字典键", type = "string")
    private String dictKey;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", type = "string")
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "integer")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string")
    private String remark;


}
