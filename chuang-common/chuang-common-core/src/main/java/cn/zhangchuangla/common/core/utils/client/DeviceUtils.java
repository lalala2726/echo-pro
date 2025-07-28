package cn.zhangchuangla.common.core.utils.client;

import cn.zhangchuangla.common.core.entity.device.BrowserDevice;
import jakarta.servlet.http.HttpServletRequest;


/**
 * @author Chuang
 * <p>
 * created on 2025/7/24 23:49
 */
public class DeviceUtils {


    /**
     * 获取浏览器设备信息
     *
     * @param userAgent User-Agent
     * @return 浏览器设备信息
     */
    public static BrowserDevice getBrowserDeviceInfo(String userAgent) {
        String browserName = UserAgentUtils.getBrowserName(userAgent);
        String osName = UserAgentUtils.getOsName(userAgent);
        String deviceType = UserAgentUtils.getDeviceType(userAgent);
        String browserVersion = UserAgentUtils.getBrowserVersion(userAgent);
        return BrowserDevice.builder()
                .browserName(browserName)
                .browserVersion(browserVersion)
                .deviceType(deviceType)
                .osName(osName)
                .build();
    }

    /**
     * 获取浏览器设备信息
     *
     * @param request HttpServletRequest
     * @return 浏览器设备信息
     */
    public static BrowserDevice getBrowserDeviceInfo(HttpServletRequest request) {
        String userAgent = UserAgentUtils.getUserAgent(request);
        return getBrowserDeviceInfo(userAgent);
    }
}
