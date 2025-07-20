package cn.zhangchuangla.system.model.request.log;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统登录日志列表请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统登录日志列表请求对象", description = "系统登录日志列表请求对象")
public class SysLoginLogQueryRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", example = "1", type = "integer", format = "int64")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin", type = "string")
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态", example = "0", type = "integer", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * ip地址
     */
    @Schema(description = "ip", example = "127.0.0.1", type = "string")
    private String address;

    /**
     * 地区
     */
    @Schema(description = "地址", example = "中国", type = "string")
    private String region;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器", example = "Chrome", type = "string")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统", example = "Windows", type = "string")
    private String os;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-01-01 00:00:00", type = "date")
    private Date loginTime;

}
