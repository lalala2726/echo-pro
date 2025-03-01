package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysLoginLog;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录日志接口
 *
 * @author zhangchuang
 */
public interface SysLoginLogService extends IService<SysLoginLog> {


    /**
     * 记录登录日志
     *
     * @param username           用户名
     * @param httpServletRequest 请求参数
     * @param loginStatus        是否登录成功
     */
    void recordLoginLog(String username, HttpServletRequest httpServletRequest, Integer loginStatus);

}

