package cn.zhangchuangla.system.model.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志视图对象
 */
@Data
@Schema(description = "系统登录日志视图对象")
public class SysLoginLogVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态")
    private Integer status;

    /**
     * ip
     */
    @Schema(description = "ip")
    private String ip;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String address;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
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
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
