package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.exception.AuthenticationException;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:50
 */
@Data
public class LoginUser implements UserDetails {

    public LoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    private SysUser sysUser;

    /**
     * 权限信息
     *
     * @return 权限信息
     */
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
        if (!sysUser.getStatus().equals(1)) {
            throw new AuthenticationException("账户已过期，请联系管理员");
        }
        return true;
    }

    /**
     * 账户是否锁定
     *
     * @return 账户是否锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        if (sysUser.getStatus() != 0) {
            throw new AuthenticationException("账户已被锁定，请联系管理员");
        }
        return true;
    }

    /**
     * 凭证是否过期
     *
     * @return 凭证是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        if (!sysUser.getStatus().equals(1)) {
            throw new AuthenticationException("凭证已过期，请重新登录");
        }
        return true;
    }

    /**
     * 是否可用
     *
     * @return 是否可用
     */
    @Override
    public boolean isEnabled() {
        if (!sysUser.getStatus().equals(1)) {
            throw new AuthenticationException("账户已被禁用，请联系管理员");
        }
        return true;
    }
}
