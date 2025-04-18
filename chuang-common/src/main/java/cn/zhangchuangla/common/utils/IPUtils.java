package cn.zhangchuangla.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.model.entity.IPEntity;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * IP工具类
 * <p>
 * 获取客户端IP地址和IP地址对应的地理位置信息
 * <p>
 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
 * </p>
 *
 * @author Ray
 * @since 2.10.0
 */
@Slf4j
@Component
//todo 在配置项设置一个是否开启Nginx代理，在之后在请求后设置一个请求头用于获取真实IP地址
public class IPUtils {

    private static final String DB_PATH = "/data/ip2region.xdb";
    private static Searcher searcher;

    /**
     * 获取IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            if (request == null) {
                return "";
            }
            ip = request.getHeader("x-forwarded-for");
            if (checkIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (checkIp(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 根据网卡取本机配置的IP
                    ip = getLocalAddr();
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR, {}", e.getMessage());
        }

        // 使用代理，则获取第一个IP地址
        if (StrUtil.isNotBlank(ip) && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    private static boolean checkIp(String ip) {
        String unknown = "unknown";
        return StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip);
    }

    /**
     * 获取本机的IP地址
     *
     * @return 本机IP地址
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error, {}", e.getMessage());
        }
        return null;
    }

    /**
     * 根据IP地址获取地理位置信息
     *
     * @param ip IP地址
     * @return 地理位置信息
     */
    public static String getRegion(String ip) {
        if (searcher == null) {
            log.error("Searcher is not initialized");
            return null;
        }

        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("IpRegionUtil ERROR, {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据IP地址获取地理位置信息
     *
     * @param ip IP地址
     * @return IPEntity对象，包含国家、省/州、城市和ISP信息
     */
    public static IPEntity getRegionEntity(String ip) {
        String region = getRegion(ip);
        IPEntity ipEntity = new IPEntity();

        if (region != null && !region.isEmpty()) {
            String[] parts = region.split("\\|");
            if (parts.length >= 5) {
                // 处理国家信息
                ipEntity.setCountry("0".equals(parts[0]) ? "" : parts[0]);

                // 处理区域信息（省/州和城市合并，中间用空格间隔）
                String areaStr = "0".equals(parts[2]) ? "" : parts[2];
                String regionStr = "0".equals(parts[3]) ? "" : parts[3];

                // 设置区域（省/州）
                ipEntity.setArea(areaStr);

                // 合并区域详情（区域+城市，用空格分隔）
                StringBuilder regionBuilder = new StringBuilder();
                if (!areaStr.isEmpty()) {
                    regionBuilder.append(areaStr);
                }
                if (!regionStr.isEmpty()) {
                    if (regionBuilder.length() > 0) {
                        regionBuilder.append(" ");
                    }
                    regionBuilder.append(regionStr);
                }
                ipEntity.setRegion(regionBuilder.toString());

                // 设置ISP信息
                ipEntity.setISP("0".equals(parts[4]) ? "" : parts[4]);
            }
        }

        return ipEntity;
    }

    @PostConstruct
    public void init() {
        try {
            // 从类路径加载资源文件
            InputStream inputStream = getClass().getResourceAsStream(DB_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + DB_PATH);
            }

            // 将资源文件复制到临时文件
            Path tempDbPath = Files.createTempFile("ip2region", ".xdb");
            Files.copy(inputStream, tempDbPath, StandardCopyOption.REPLACE_EXISTING);

            // 使用临时文件初始化 Searcher 对象
            searcher = Searcher.newWithFileOnly(tempDbPath.toString());
        } catch (Exception e) {
            log.error("IpRegionUtil initialization ERROR, {}", e.getMessage());
        }
    }
}
