package cn.zhangchuangla.system.core.model.vo.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

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
    @Schema(description = "主键ID", type = "integer", format = "int64", example = "1")
    private Long id;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", type = "string", examples = "sys_user_sex")
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", type = "string", example = "用户性别")
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", type = "integer", examples = "0")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", example = "this is a dict")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", examples = "2023-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2023-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人", type = "string", example = "admin")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人", type = "string", example = "admin")
    private String updateBy;
}
