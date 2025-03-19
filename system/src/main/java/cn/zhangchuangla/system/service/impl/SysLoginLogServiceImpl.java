package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.SysConstant;
import cn.zhangchuangla.system.mapper.SysLoginLogMapper;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.service.SysLoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务实现类
 *
 * @author zhangchuang
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog>
        implements SysLoginLogService {

    /**
     * 记录登录日志
     *
     * @param username           用户
     * @param httpServletRequest 请求参数
     * @param loginStatus        是否登录成功
     */
    @Override
    public void recordLoginLog(String username, HttpServletRequest httpServletRequest, Integer loginStatus) {
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setStatus(loginStatus);
        sysLoginLog.setUsername(username);
        sysLoginLog.setIp(httpServletRequest.getRemoteAddr());
        sysLoginLog.setAddress(httpServletRequest.getRemoteAddr());
        sysLoginLog.setBrowser(httpServletRequest.getHeader("User-Agent"));
        sysLoginLog.setOs(httpServletRequest.getHeader("sec-ch-ua-platform"));
        sysLoginLog.setCreateBy(SysConstant.SYSTEM_CREATE);
        save(sysLoginLog);
    }
}




