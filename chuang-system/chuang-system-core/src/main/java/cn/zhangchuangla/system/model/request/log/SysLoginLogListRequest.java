package cn.zhangchuangla.system.model.request.log;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    /**
     * ip
     */
    @Schema(description = "ip", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    /**
     * 地址
     */
    @Schema(description = "地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String address;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String os;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date createTime;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
