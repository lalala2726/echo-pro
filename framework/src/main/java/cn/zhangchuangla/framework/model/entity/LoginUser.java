package cn.zhangchuangla.framework.model.entity;

import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:50
 */
@Data
public class LoginUser implements UserDetails, Serializable {


    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    public Long userId;
    @Resource
    private RedisCache redisCache;
    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private SysUser sysUser;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 登录IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String sessionId;
    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    private long loginTime;
    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private long expireTime;
    /**
     * 浏览器信息
     */
    @Schema(description = "浏览器信息")
    private String browser;
    /**
     * 操作系统信息
     */
    @Schema(description = "操作系统信息")
    private String os;
    /**
     * 角色信息
     */
    @Schema(description = "角色信息")
    private List<SysRole> roles = new ArrayList<>();
    /**
     * 权限信息
     */
    @Schema(description = "权限信息")
    private List<SysPermissions> permissions = new ArrayList<>();

    public LoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * 密码
     *
     * @return 密码
     */
    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    /**
     * 用户名
     *
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    /**
     * 账户是否过期
     *
     * @return 账户是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否锁定
     *
     * @return 账户是否锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return Objects.equals(sysUser.getStatus(), SystemConstant.ACCOUNT_UNLOCK_KEY);
    }

    /**
     * 凭证是否过期
     *
     * @return 凭证是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用
     *
     * @return 是否可用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
