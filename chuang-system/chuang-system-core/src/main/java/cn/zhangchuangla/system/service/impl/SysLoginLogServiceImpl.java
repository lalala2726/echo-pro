package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.model.entity.ClientInfo;
import cn.zhangchuangla.common.utils.ClientUtils;
import cn.zhangchuangla.system.mapper.SysLoginLogMapper;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogListRequest;
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
 * @author zhangchuang
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
        ClientInfo deviceInfo = ClientUtils.getClientInfo(httpServletRequest);
        log.info("用户名: {},登录: {},登录时间: {},系统名称: {}, IP地址: {}, 浏览器名称: {}, 设备版本: {}, 浏览器类型: {}, 设备类型: {}, 浏览器渲染引擎: {}, 区域: {}",
                username,
                isSuccess ? "登录成功" : "登录失败",
                new Date(),
                deviceInfo.getOsName(),
                deviceInfo.getIp(),
                deviceInfo.getBrowserName(),
                deviceInfo.getOsVersion(),
                deviceInfo.getBrowserType(),
                deviceInfo.getDeviceType(),
                deviceInfo.getBrowserRenderingEngine(),
                deviceInfo.getRegion()
        );
        SysLoginLog sysLoginLog = SysLoginLog.builder()
                .os(deviceInfo.getOsName())
                .ip(deviceInfo.getIp())
                .address(deviceInfo.getRegion())
                .username(username)
                .browser(deviceInfo.getBrowserName())
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
    public Page<SysLoginLog> listLoginLog(SysLoginLogListRequest request) {
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




