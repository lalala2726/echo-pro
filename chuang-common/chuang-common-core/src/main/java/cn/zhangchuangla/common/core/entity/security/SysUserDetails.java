package cn.zhangchuangla.common.core.entity.security;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
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
     * 部门ID
     */
    private Long deptId;


    /**
     * 用户角色权限集合
     */
    private Collection<SimpleGrantedAuthority> authorities;


    public SysUserDetails() {

    }

    /**
     * 构造函数
     *
     * @param sysUser 用户信息
     * @param roles   角色集合
     */
    public SysUserDetails(SysUser sysUser, Set<String> roles) {
        this.sysUser = sysUser;
        this.userId = sysUser.getUserId();
        this.deptId = sysUser.getDeptId();
        this.username = sysUser.getUsername();
        // 初始化角色权限集合
        this.authorities = CollectionUtils.isNotEmpty(roles)
                ? roles.stream()
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
        return username;
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
