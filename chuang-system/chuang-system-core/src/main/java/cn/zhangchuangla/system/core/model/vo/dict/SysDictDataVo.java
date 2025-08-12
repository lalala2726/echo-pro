package cn.zhangchuangla.system.core.model.vo.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 字典数据视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "字典数据视图对象", description = "字典数据视图对象")
public class SysDictDataVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64", example = "1")
    private Long id;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", type = "string", example = "sys_user_sex")
    private String dictType;

    /**
     * 字典标签
     */
    @Schema(description = "字典标签", type = "string", example = "男")
    private String dictLabel;

    /**
     * 字典值
     */
    @Schema(description = "字典值", type = "string", example = "1")
    private String dictValue;


    /**
     * 颜色
     */
    @Schema(description = "颜色", type = "string", example = "{#000000}")
    private String color;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "integer", example = "1")
    private Integer sort;

    /**
     * 状态：1启用，0禁用
     */
    @Schema(description = "状态：1启用，0禁用", type = "integer", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", example = "this is a dict")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string", example = "2023-01-01 00:00:00")
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
