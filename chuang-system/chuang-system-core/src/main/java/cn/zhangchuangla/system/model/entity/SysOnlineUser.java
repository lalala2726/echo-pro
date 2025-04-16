package cn.zhangchuangla.system.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * 在线用户实体
 *
 * @author Chuang
 * <p>
 * created on 2025/3/20 13:09
 */
@Data
public class SysOnlineUser {

    /**
     * 会话ID
     */
    private String sessionId;


    /**
     * 用户名
     */
    private String username;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 登录地点
     */
    private String address;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 账户是否过期
     */
    private boolean accountNonExpired;

    /**
     * 账户是否被锁定
     */
    private boolean accountNonLocked;

    /**
     * 账户密码是否过期
     */
    private boolean enabled;

}
