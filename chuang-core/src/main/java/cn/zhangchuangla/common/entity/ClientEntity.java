package cn.zhangchuangla.common.entity;

import lombok.Data;

/**
 * 记录客户端信息的实体类
 * <p>
 * 该类用于存储客户端的相关信息，例如 IP 地址、浏览器信息、操作系统等，
 * 便于日志记录、用户行为分析以及安全审计。
 *
 * @author Chuang
 * @version 1.0
 * @since 2025/2/19 20:33
 */
@Data
public class ClientEntity {

    /**
     * 客户端 IP 地址
     * 可能是 IPv4 或 IPv6 格式
     */
    private String ip;

    /**
     * 客户端的地理位置信息
     * 例如："中国 北京"、"美国 纽约"
     */
    private String address;

    /**
     * 客户端使用的浏览器类型
     * 例如："Chrome 120.0"、"Firefox 98.0"
     */
    private String browser;

    /**
     * 客户端的操作系统信息
     * 例如："Windows 10"、"macOS 13"、"Android 12"
     */
    private String os;

    /**
     * 客户端设备类型
     * 例如："PC"、"Mobile"、"Tablet"
     */
    private String deviceType;

    /**
     * 客户端 User-Agent 信息
     * 包含完整的浏览器、设备、系统等信息
     */
    private String userAgent;
}
