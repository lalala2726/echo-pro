package cn.zhangchuangla.common.core.security.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SecurityConstants;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:50
 */
@Data
public class SysUserDetails implements UserDetails, Serializable {


    @Serial
    private static final long serialVersionUID = -5777762905473897401L;

    /**
     * 用户ID
     */
    public Long userId;

    /**
     * 用户信息
     */
    private SysUser sysUser;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP地址
     */
    private String ip;

    /**
     * 登录地点
     */
    private String address;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 登录时间
     */
    private long loginTime;

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * 操作系统信息
     */
    private String os;


    /**
     * 用户角色权限集合
     */
    private Collection<SimpleGrantedAuthority> authorities;

    public SysUserDetails() {

    }

    public SysUserDetails(SysUser sysUser, Set<String> roles) {
        this.sysUser = sysUser;
        // 初始化角色权限集合
        this.authorities = CollectionUtil.isNotEmpty(roles)
                ? roles.stream()
                // 角色名加上前缀 "ROLE_"，用于区分角色 (ROLE_ADMIN) 和权限 (user:add)
                .map(role -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role))
                .collect(Collectors.toSet())
                : Collections.emptySet();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
        return Objects.equals(sysUser.getStatus(), Constants.ACCOUNT_UNLOCK_KEY);
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
