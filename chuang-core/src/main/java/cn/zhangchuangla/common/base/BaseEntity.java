package cn.zhangchuangla.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类,
 */
@Data
@Schema(name = "基础实体类")
public class BaseEntity implements Serializable {

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    private Date createTime;

    /**
     * 删除时间
     */
    @Schema(name = "删除时间")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(name = "创建人")
    private String createBy;

    /**
     * 修改人
     */
    @Schema(name = "修改人")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(name = "备注")
    private String remark;


}
