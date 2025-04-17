package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.model.entity.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 16:20
 */
public class ClientUtils {


    /**
     * 获取客户端参数
     *
     * @param request HttpServletRequest
     * @return 客户端信息
     */
    public static ClientInfo getClientInfo(HttpServletRequest request) {
        String ipAddr = IPUtils.getIpAddr(request);

        String userAgent = UserAgentUtils.getUserAgent(request);
        UserAgentUtils.getDeviceManufacturer(userAgent);

        String region = IPUtils.getRegion(ipAddr);
        String osName = UserAgentUtils.getOsName(userAgent);
        String browserName = UserAgentUtils.getBrowserName(userAgent);
        String osVersion = UserAgentUtils.getOsVersion(userAgent);
        String browserType = UserAgentUtils.getBrowserType(userAgent);
        String deviceType = UserAgentUtils.getDeviceType(userAgent);
        String browserRenderingEngine = UserAgentUtils.getBrowserRenderingEngine(userAgent);

        return ClientInfo.builder()
                .ip(ipAddr)
                .region(region)
                .osName(osName)
                .browserName(browserName)
                .osVersion(osVersion)
                .browserType(browserType)
                .deviceType(deviceType)
                .browserRenderingEngine(browserRenderingEngine)
                .build();
    }
}
