package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.system.mapper.SysLoginLogMapper;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogQueryRequest;
import cn.zhangchuangla.system.service.SysLoginLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 登录日志服务实现类
 *
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog>
        implements SysLoginLogService {

    private final SysLoginLogMapper sysLoginLogMapper;

    /**
     * 记录登录日志
     *
     * @param username           用户
     * @param httpServletRequest 请求参数
     * @param isSuccess          是否登录成功
     */
    @Override
    public void recordLoginLog(String username, HttpServletRequest httpServletRequest, boolean isSuccess) {
        String ipAddr = IPUtils.getIpAddr(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        UserAgentUtils.getDeviceManufacturer(userAgent);

        String region = IPUtils.getRegion(ipAddr);
        String osName = UserAgentUtils.getOsName(userAgent);
        String browserName = UserAgentUtils.getBrowserName(userAgent);
        String osVersion = UserAgentUtils.getOsVersion(userAgent);
        String browserType = UserAgentUtils.getBrowserType(userAgent);
        String deviceType = UserAgentUtils.getDeviceType(userAgent);
        String browserRenderingEngine = UserAgentUtils.getBrowserRenderingEngine(userAgent);

        log.info("用户名: {},登录: {},登录时间: {},系统名称: {}, IP地址: {}, 浏览器名称: {}, 设备版本: {}, 浏览器类型: {}, 设备类型: {}, 浏览器渲染引擎: {}, 区域: {}",
                username,
                isSuccess ? "登录成功" : "登录失败",
                new Date(),
                osName,
                ipAddr,
                browserName,
                osVersion,
                browserType,
                deviceType,
                browserRenderingEngine,
                region
        );


        // 记录登录日志
        SysLoginLog sysLoginLog = SysLoginLog.builder()
                .os(osName)
                .ip(ipAddr)
                .region(region)
                .username(username)
                .browser(browserName)
                .status(isSuccess ? 0 : 1)
                .createBy(Constants.SYSTEM_CREATE)
                .build();
        save(sysLoginLog);
    }


    /**
     * 分页查询登录日志
     *
     * @param request 查询参数
     * @return 登录日志列表
     */
    @Override
    public Page<SysLoginLog> listLoginLog(SysLoginLogQueryRequest request) {
        Page<SysLoginLog> sysLoginLogPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysLoginLogMapper.listLoginLog(sysLoginLogPage, request);
    }


    /**
     * 清空登录日志
     *
     * @return 是否成功
     */
    @Override
    public boolean cleanLoginLog() {
        //删除所有日志
        sysLoginLogMapper.cleanLoginLog();
        return true;
    }

    /**
     * 根据ID获取登录日志
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public SysLoginLog getLoginLogById(Long id) {
        return getById(id);
    }
}




