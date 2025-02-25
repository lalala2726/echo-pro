package cn.zhangchuangla.system.model.vo;

import cn.zhangchuangla.common.base.BaseVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "用户VO", description = "返回前端展示的数据")
public class SysUserVo extends BaseVO {

    /**
     * ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;


    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
