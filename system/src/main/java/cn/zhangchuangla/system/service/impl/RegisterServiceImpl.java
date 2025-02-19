package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.system.model.entity.SysUser;
import cn.zhangchuangla.system.model.request.RegisterRequest;
import cn.zhangchuangla.system.service.RegisterService;
import cn.zhangchuangla.system.service.SysUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final SysUserService sysUserService;

    public RegisterServiceImpl(BCryptPasswordEncoder passwordEncoder, SysUserService sysUserService) {
        this.passwordEncoder = passwordEncoder;
        this.sysUserService = sysUserService;
    }

    public Long register(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new ParamException(ResponseCode.PARAM_ERROR);
        }
        // 加密密码
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 创建用户对象
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setCreateTime(new Date());
        user.setStatus(0); // 0-正常 1-停用

        sysUserService.save(user);

        return user.getId();
    }
}
