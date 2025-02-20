package cn.zhangchuangla.framework.model.entity;

import cn.zhangchuangla.system.model.entity.SysUser;
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

    private SysUser sysUser;

    public LoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

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
        return true;
    }

    /**
     * 账户是否锁定
     *
     * @return 账户是否锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
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
