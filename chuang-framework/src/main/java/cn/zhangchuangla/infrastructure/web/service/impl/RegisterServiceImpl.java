package cn.zhangchuangla.infrastructure.web.service.impl;

import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.infrastructure.model.request.RegisterRequest;
import cn.zhangchuangla.infrastructure.web.service.RegisterService;
import cn.zhangchuangla.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final SysUserService sysUserService;

    @Autowired
    public RegisterServiceImpl(BCryptPasswordEncoder passwordEncoder, SysUserService sysUserService) {
        this.passwordEncoder = passwordEncoder;
        this.sysUserService = sysUserService;
    }

    /**
     * 注册用户
     *
     * @param request 请求参数
     * @return 用户ID
     */
    public Long register(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new ParamException(ResponseCode.PARAM_ERROR);
        }
        if (sysUserService.isUsernameExist(request.getUsername())) {
            throw new ServiceException(String.format("用户名%s已存在", request.getUsername()));
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setCreateTime(new Date());
        user.setStatus(0); // 0-正常 1-停用
        sysUserService.save(user);
        return user.getUserId();
    }
}
