package cn.zhangchuangla.common.core.entity.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chuang
 */
@Data
@Schema(description = "基础视图对象")
public class BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1128600362851925919L;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string")
    private String remark;

    /**
     * 创建者
     */
    @Schema(description = "创建者", type = "string")
    private String createBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者", type = "string")
    private String updateBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", type = "string")
    private Date updateTime;
}
