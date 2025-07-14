package cn.zhangchuangla.system.model.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "字典类型视图对象", description = "字典类型视图对象")
public class SysDictTypeVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64")
    private Long id;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", type = "string")
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", type = "string")
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "integer")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人", type = "string")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人", type = "string")
    private String updateBy;
} 