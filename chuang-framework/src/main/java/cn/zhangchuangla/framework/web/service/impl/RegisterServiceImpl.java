package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.core.core.security.model.SysUser;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.framework.model.request.RegisterRequest;
import cn.zhangchuangla.framework.web.service.RegisterService;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final SysUserService sysUserService;

    /**
     * 注册用户
     *
     * @param request 请求参数
     * @return 用户ID
     */
    @Override
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
        // 0-正常 1-停用
        user.setStatus(0);
        sysUserService.save(user);
        return user.getUserId();
    }
}
