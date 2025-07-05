package cn.zhangchuangla.system.model.request.log;

import cn.zhangchuangla.common.core.core.entity.base.BasePageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

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
    @Min(value = 1, message = "主键不能小于1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin", type = "string")
    @Size(max = 64, min = 1, message = "用户名长度在1-64个字符")
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态", example = "0", type = "integer", allowableValues = {"0", "1"})
    @Range(min = 0, max = 1, message = "账号状态只能为0或1")
    @JsonSerialize(using = ToStringSerializer.class)
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
