package cn.zhangchuangla.system.model.request.log;

import cn.zhangchuangla.common.base.BasePageRequest;
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
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "系统登录日志列表请求对象")
public class SysLoginLogListRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "主键不能小于1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 64, min = 1, message = "用户名长度在1-64个字符")
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Range(min = 0, max = 1, message = "账号状态只能为0或1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * ip地址
     */
    @Schema(description = "ip", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String address;

    /**
     * 地区
     */
    @Schema(description = "地址", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String region;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String os;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date loginTime;

}
