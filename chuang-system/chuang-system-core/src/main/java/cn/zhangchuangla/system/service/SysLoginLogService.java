package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * @param isSuccess          是否登录成功
     */
    void recordLoginLog(String username, HttpServletRequest httpServletRequest, boolean isSuccess);

    /**
     * 分页查询登录日志
     *
     * @param request 查询参数
     * @return 登录日志列表
     */
    Page<SysLoginLog> listLoginLog(SysLoginLogListRequest request);

    /**
     * 清空登录日志
     *
     * @return 是否成功
     */
    boolean cleanLoginLog();

    /**
     * 根据ID获取登录日志
     *
     * @param id 主键
     * @return 是否成功
     */
    SysLoginLog getLoginLogById(Long id);
}

