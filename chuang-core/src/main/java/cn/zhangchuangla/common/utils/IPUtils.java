package cn.zhangchuangla.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;


/**
 * 客户端工具类
 * <p>
 * 提供获取客户端 IP 地址、User-Agent 信息、浏览器、操作系统等功能
 *
 * @author Chuang
 * @version 1.0
 * @since 2025/3/19 15:11
 */
public class IPUtils {

    /**
     * 获取客户端真实 IP 地址
     *
     * @param request HttpServletRequest 请求对象
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后，会有多个 IP，取第一个非 unknown 的 IP 作为客户端真实 IP
            int index = ip.indexOf(",");
            if (index != -1) {
                ip = ip.substring(0, index).trim();
            }
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取客户端 User-Agent 信息
     *
     * @param request HttpServletRequest 请求对象
     * @return User-Agent 信息
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        return StringUtils.defaultIfEmpty(request.getHeader("User-Agent"), "unknown");
    }

    /**
     * 根据 IP 获取地址（占位方法，可调用第三方 IP 定位接口实现）
     *
     * @param ip IP 地址
     * @return 地理位置（例如：中国 北京 / 美国 纽约）
     */
    public static String getAddressByIp(String ip) {
        if ("127.0.0.1".equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            return "本地访问";
        }
        // 此处可调用第三方 IP 定位接口
        // 例如：阿里云、腾讯位置服务、百度 API 等
        return "未知位置";
    }
}
