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
    @Schema(description = "会话ID")
    private String sessionId;

    /**
     * 会话过期时间
     */
    @Schema(description = "会话过期时间")
    private String sessionExpireTime;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 登录IP
     */
    @Schema(description = "登录IP")
    private String ip;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
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
     * 登录时间
     */
    @Schema(description = "登录时间")
    private String loginTime;

    /**
     * 账户是否过期
     */
    @Schema(description = "账户是否过期")
    private boolean accountNonExpired;

    /**
     * 账户是否被锁定
     */
    @Schema(description = "账户是否被锁定")
    private boolean accountNonLocked;

    /**
     * 账户密码是否过期
     */
    @Schema(description = "账户密码是否过期")
    private boolean enabled;

}
