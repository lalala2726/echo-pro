package cn.zhangchuangla.system.model.request.monitor;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 13:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OnlineUserListRequest extends BasePageRequest {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sessionId;

    /**
     * 会话过期时间
     */
    @Schema(description = "会话过期时间", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sessionExpireTime;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    /**
     * 登录IP
     */
    @Schema(description = "登录IP", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String address;

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
     * 登录时间
     */
    @Schema(description = "登录时间", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String loginTime;


}
