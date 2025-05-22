package cn.zhangchuangla.common.core.core.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 在线用户信息对象
 *
 * @author Chuang
 * <p>
 * created on 2025/2/27 10:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineLoginUser {


    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String sessionId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 角色权限集合
     */
    @Schema(description = "角色权限集合")
    private Set<String> roles;

    /**
     * 登录IP地址
     */
    @Schema(description = "登录IP地址")
    private String IP;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
    private String region;

    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    private Long loginTime;

    /**
     * userAgent
     */
    @Schema(description = "userAgent")
    private String userAgent;

    /**
     * 设备信息
     */
    @Schema(description = "设备信息")
    private String device;

    /**
     * 设备
     */
    @Schema(description = "设备")
    private String os;

    /**
     * 浏览器信息
     */
    @Schema(description = "浏览器信息")
    private String browser;


}
