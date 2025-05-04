package cn.zhangchuangla.system.model.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统字典类型表
 */
@Data
@Schema(description = "系统字典列表视图对象")
public class SysDictTypeVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64")
    private Long id;

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", type = "string")
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", type = "string")
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "string")
    private String status;

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