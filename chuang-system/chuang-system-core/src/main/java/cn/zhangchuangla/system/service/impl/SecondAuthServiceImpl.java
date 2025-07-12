package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.system.service.SecondAuthService;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/10 00:42
 */
@Service
@RequiredArgsConstructor
public class SecondAuthServiceImpl implements SecondAuthService {

    private final SysUserService sysUserService;

    /**
     * 验证当前用户的密码
     *
     * @param submittedPassword 提交的密码
     * @return 如果密码正确，返回true；否则返回false
     */
    @Override
    public boolean verifyCurrentUserPassword(String submittedPassword) {
        // 1. 获取当前用户的密码
        Long userId = SecurityUtils.getUserId();
        SysUser userInfoByUserId = sysUserService.getUserInfoByUserId(userId);
        if (userInfoByUserId == null) {
            throw new SecurityException("用户不存在或未登录。");
        }
        return SecurityUtils.matchesPassword(submittedPassword, userInfoByUserId.getPassword());
    }
}
