package cn.zhangchuangla.system.core.model.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志列表视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统登录日志列表视图对象", description = "系统登录日志列表视图对象")
public class SysLoginLogListVo {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "number", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", example = "admin")
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态", type = "number", example = "1")
    private Integer status;

    /**
     * 登录IP
     */
    @Schema(description = "ip", type = "string", example = "127.0.0.1")
    private String ip;

    /**
     * 地址
     */
    @Schema(description = "地址", type = "string", example = "中国")
    private String region;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器", type = "string", example = "Chrome")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统", type = "string", example = "Windows")
    private String os;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string")
    private Date loginTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者", type = "string", example = "admin")
    private String createBy;

}
