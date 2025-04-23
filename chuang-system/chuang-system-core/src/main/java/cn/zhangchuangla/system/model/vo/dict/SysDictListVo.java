package cn.zhangchuangla.system.model.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 字典表
 */
@Data
public class SysDictListVo {

    /**
     * 主键
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码")
    private String dictCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称")
    private String name;

    /**
     * 状态(0:正常;1:禁用)
     */
    @Schema(description = "状态(0:正常;1:禁用)")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

}
