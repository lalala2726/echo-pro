package cn.zhangchuangla.common.core.model.entity;

import cn.zhangchuangla.common.constant.SystemConstant;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:50
 */
@Data
public class LoginUser implements UserDetails, Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    public LoginUser() {
    }
    

    public LoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }


    /**
     * 用户信息
     */
    private SysUser sysUser;


    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    public Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP地址
     */
    private String ipAddress;

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


    @JSONField(serialize = false, deserialize = false)
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
