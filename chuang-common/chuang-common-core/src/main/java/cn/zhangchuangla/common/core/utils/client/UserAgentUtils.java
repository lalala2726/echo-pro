package cn.zhangchuangla.common.core.utils.client;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * UserAgent 工具类
 * <p>
 * 解析客户端 User-Agent 信息，提取浏览器、操作系统、设备类型等详细信息
 *
 * @author Chuang
 * @version 2.0
 * @since 2025/03/19
 */
public class UserAgentUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserAgentUtils.class);

    /**
     * 从 HttpServletRequest 中获取 User-Agent 字符串
     *
     * @param request HttpServletRequest 请求对象
     * @return User-Agent 字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 解析 User-Agent 返回 UserAgent 对象
     *
     * @param userAgentStr User-Agent 字符串
     * @return UserAgent 对象
     */
    public static UserAgent parseUserAgent(String userAgentStr) {
        if (StringUtils.isBlank(userAgentStr)) {
            return UserAgent.parseUserAgentString("unknown");
        }
        return UserAgent.parseUserAgentString(userAgentStr);
    }

    /**
     * 获取操作系统对象
     *
     * @param userAgentStr User-Agent 字符串
     * @return OperatingSystem 操作系统对象
     */
    private static OperatingSystem getOperatingSystem(String userAgentStr) {
        return parseUserAgent(userAgentStr).getOperatingSystem();

    }

    /**
     * 获取操作系统对象
     *
     * @param request HttpServletRequest
     * @return OperatingSystem 操作系统对象
     */
    private static OperatingSystem getOperatingSystem(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        return parseUserAgent(userAgent).getOperatingSystem();
    }

    /**
     * 获取浏览器对象
     *
     * @param userAgentStr User-Agent 字符串
     * @return Browser 浏览器对象
     */
    private static Browser getBrowser(String userAgentStr) {
        return parseUserAgent(userAgentStr).getBrowser();
    }

    /**
     * 获取设备类型
     *
     * @param userAgentStr User-Agent 字符串
     * @return 设备类型：Mobile / Tablet / Computer
     */
    public static String getDeviceType(String userAgentStr) {
        DeviceType deviceType = getOperatingSystem(userAgentStr).getDeviceType();
        return deviceType.getName();
    }

    /**
     * 获取操作系统名称
     *
     * @param userAgentStr User-Agent 字符串
     * @return 操作系统名称
     */
    public static String getOsName(String userAgentStr) {
        return getOperatingSystem(userAgentStr).getName();
    }

    /**
     * 获取操作系统版本信息（仅限 Windows/Linux/Android/iOS）
     *
     * @param userAgentStr User-Agent 字符串
     * @return 操作系统版本
     */
    public static String getOsVersion(String userAgentStr) {
        if (StringUtils.isBlank(userAgentStr)) {
            return "unknown";
        }

        String osVersion = "unknown";
        try {
            String[] strArr = userAgentStr.substring(userAgentStr.indexOf("(") + 1,
                    userAgentStr.indexOf(")")).split(";");
            if (strArr.length > 1) {
                osVersion = strArr[1].trim();
            }
        } catch (Exception e) {
            logger.error("获取操作系统版本失败", e);
        }

        return osVersion;
    }

    /**
     * 获取浏览器名称
     *
     * @param userAgentStr User-Agent 字符串
     * @return 浏览器名称
     */
    public static String getBrowserName(String userAgentStr) {
        return getBrowser(userAgentStr).getName();
    }

    /**
     * 获取浏览器类型（例如：Web Browser / Mobile Browser）
     *
     * @param userAgentStr User-Agent 字符串
     * @return 浏览器类型
     */
    public static String getBrowserType(String userAgentStr) {
        return getBrowser(userAgentStr).getBrowserType().getName();
    }

    /**
     * 获取浏览器版本号
     *
     * @param userAgentStr User-Agent 字符串
     * @return 浏览器版本号
     */
    public static String getBrowserVersion(String userAgentStr) {
        return getBrowser(userAgentStr).getVersion(userAgentStr).toString();
    }

    /**
     * 获取浏览器生产厂商
     *
     * @param userAgentStr User-Agent 字符串
     * @return 浏览器生产厂商
     */
    public static String getBrowserManufacturer(String userAgentStr) {
        return getBrowser(userAgentStr).getManufacturer().getName();
    }

    /**
     * 获取浏览器渲染引擎
     *
     * @param userAgentStr User-Agent 字符串
     * @return 渲染引擎名称
     */
    public static String getBrowserRenderingEngine(String userAgentStr) {
        return getBrowser(userAgentStr).getRenderingEngine().name();
    }

    /**
     * 获取设备的生产厂家
     *
     * @param userAgentStr User-Agent 字符串
     * @return 设备生产厂家
     */
    public static String getDeviceManufacturer(String userAgentStr) {
        return getOperatingSystem(userAgentStr).getManufacturer().toString();
    }

    /**
     * 判断是否为移动设备
     *
     * @param userAgentStr User-Agent 字符串
     * @return true：移动设备；false：非移动设备
     */
    public static boolean isMobileDevice(String userAgentStr) {
        DeviceType deviceType = getOperatingSystem(userAgentStr).getDeviceType();
        return deviceType.equals(DeviceType.MOBILE) || deviceType.equals(DeviceType.TABLET);
    }


}
