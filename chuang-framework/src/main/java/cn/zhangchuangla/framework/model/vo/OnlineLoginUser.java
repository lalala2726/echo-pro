package cn.zhangchuangla.framework.model.vo;

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
     * 访问令牌ID
     */
    @Schema(description = "会话ID")
    private String accessTokenId;

    /**
     * 刷新令牌ID
     */
    @Schema(description = "刷新令牌ID")
    private String refreshTokenId;

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
    private String ip;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
    private String location;

    /**
     * 访问时间
     */
    @Schema(description = "最近访问时间")
    private Long accessTime;

    /**
     * userAgent
     */
    @Schema(description = "userAgent")
    private String userAgent;


}
