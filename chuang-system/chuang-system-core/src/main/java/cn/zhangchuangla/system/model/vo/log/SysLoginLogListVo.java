package cn.zhangchuangla.system.model.vo.log;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志列表视图对象
 *
 * @author zhangchuang
 */
@Data
@Schema(name = "系统登录日志列表视图对象", description = "系统登录日志列表视图对象")
public class SysLoginLogListVo {

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
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;


    /**
     * 登录IP
     */
    @Schema(description = "ip")
    private String ip;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String region;

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
    private Date loginTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

}
