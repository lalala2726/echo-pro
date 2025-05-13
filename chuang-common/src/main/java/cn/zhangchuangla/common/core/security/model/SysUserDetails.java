package cn.zhangchuangla.common.core.security.model;

import cn.zhangchuangla.common.constant.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Spring Security 用户详情对象
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:50
 */
@Data
@Slf4j
public class SysUserDetails implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = -5777762905473897401L;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 用户角色原始集合（不含前缀）
     */
    private Set<String> roles;

    /**
     * 用户角色权限集合
     */
    private Collection<SimpleGrantedAuthority> authorities;

    public SysUserDetails() {
    }

    /**
     * 构造方法
     *
     * @param sysUser 系统用户对象
     */
    public SysUserDetails(SysUser sysUser) {
        this.sysUser = sysUser;
        this.userId = sysUser.getUserId();
        this.deptId = sysUser.getDeptId();
        this.username = sysUser.getUsername();
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

    /**
     * 判断是否有指定角色
     *
     * @param role 角色标识
     * @return 是否拥有角色
     */
    public boolean hasRole(String role) {
        return this.roles != null && this.roles.contains(role);
    }
}
