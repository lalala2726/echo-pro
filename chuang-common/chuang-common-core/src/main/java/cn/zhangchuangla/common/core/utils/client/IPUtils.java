package cn.zhangchuangla.common.core.utils.client;

import org.apache.commons.lang3.StringUtils;
import cn.zhangchuangla.common.core.model.entity.IPEntity;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * IP工具类 (优化版)
 * <p>
 * 获取客户端IP地址和IP地址对应的地理位置信息
 * <p>
 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
 * </p>
 *
 * @author Ray (Original), Chuang (Refined)
 * @since 2.10.0
 */
@Slf4j
@Component
public class IPUtils {

    // 注意：确保 ip2region.xdb 文件在指定的 classpath 路径下，或者调整加载方式
    private static final String DB_CLASSPATH_PATH = "/data/ip2region.xdb";
    private static final String UNKNOWN_IP_INFO = "未知";
    // 或 "内网"
    private static final String PRIVATE_IP_INFO = "局域网";
    private static final String LOCALHOST_IO_INFO = "本机";
    // 定义运营商NAT的标识
    private static final String CGN_IP_INFO = "运营商NAT";
    private static Searcher searcher;

    // --- IP 地址获取逻辑 (基本不变) ---

    /**
     * 获取IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip;
        try {
            if (request == null) {
                return "";
            }
            // 尝试从常见的代理头获取 IP
            ip = request.getHeader("X-Forwarded-For");
            if (!isValidIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (!isValidIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (!isValidIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (!isValidIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            // 如果以上都无效，获取直接连接的 IP
            if (!isValidIp(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 根据网卡取本机配置的IP
                    ip = getLocalAddr();
                }
            }
        } catch (Exception e) {
            // 记录完整异常
            log.error("IPUtils getIpAddr ERROR", e);
            // 出错时返回空字符串
            return "";
        }

        // 处理多级代理情况，取第一个有效 IP
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String singleIp : ips) {
                if (isValidIp(singleIp.trim())) {
                    ip = singleIp.trim();
                    break;
                }
            }
            // 如果分割后都无效，则保留原始处理的第一个（可能就是无效的）
            if (!isValidIp(ip)) {
                // 或返回空？取决于业务需求
                ip = ips[0].trim();
            }
        }
        return ip;
    }

    /**
     * 检查 IP 是否有效（非空、非 "unknown"）
     */
    private static boolean isValidIp(String ip) {
        String unknown = "unknown";
        return StringUtils.isNotBlank(ip) && !unknown.equalsIgnoreCase(ip);
    }


    /**
     * 获取本机的IP地址
     *
     * @return 本机IP地址，获取失败返回 null
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error", e);
        }
        return null;
    }

    // --- IP 地理位置查询逻辑 ---

    /**
     * 根据 IP 地址获取原始的 ip2region 地理位置字符串
     * （内部使用，优先调用 getRegionEntity 获取结构化数据）
     *
     * @param ip IP地址
     * @return 地理位置原始字符串，查询失败或 searcher 未初始化返回 null
     */
    private static String getRegionString(String ip) {
        if (searcher == null) {
            log.error("Ip2region searcher is not initialized.");
            return null;
        }
        // 基础校验
        if (StringUtils.isBlank(ip)) {
            return null;
        }

        try {
            String region = searcher.search(ip);
            // ip2region 可能返回 null 或空字符串
            return StringUtils.isBlank(region) ? null : region;
        } catch (Exception e) {
            log.error("Ip2region search error for IP: {}", ip, e);
            return null;
        }
    }

    /**
     * 判断是否为私有地址或运营商级NAT地址 (IPv4)
     *
     * @param ip IPv4 地址字符串
     * @return boolean
     */
    private static boolean isPrivateOrCgnIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        try {
            String[] octets = ip.split("\\.");
            if (octets.length != 4) {
                // 非标准 IPv4 格式
                return false;
            }

            int o1 = Integer.parseInt(octets[0]);
            int o2 = Integer.parseInt(octets[1]);

            // A类IP地址: 10.0.0.0-10.255.255.255
            if (o1 == 10) {
                return true;
            }

            // B类IP地址: 172.16.0.0-172.31.255.255
            if (o1 == 172 && (o2 >= 16 && o2 <= 31)) {
                return true;
            }

            // C类IP地址: 192.168.0.0-192.168.255.255
            if (o1 == 192 && o2 == 168) {
                return true;
            }

            // CGN（运营商级NAT）: 100.64.0.0 - 100.127.255.255 (RFC 6598)
            if (o1 == 100 && (o2 >= 64 && o2 <= 127)) {
                return true;
            }
            //本地回环地址
            if (o1 == 127) {
                return true;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid IP format for private check: {}", ip);
            // 格式错误视为非私有
            return false;
        }
        return false;
    }

    /**
     * 根据IP返回地理位置信息，例如: 国家 省份 城市 ISP
     *
     * @param ip IP地址（目前仅支持IPV4）
     * @return 地理位置信息字符串，格式为 "国家 省份 城市 ISP"
     */
    public static String getRegion(String ip) {
        IPEntity regionEntity = getRegionEntity(ip);
        StringBuilder region = new StringBuilder();

        if (StringUtils.isNotBlank(regionEntity.getCountry()) && !UNKNOWN_IP_INFO.equals(regionEntity.getCountry())) {
            region.append(regionEntity.getCountry());
        }

        if (StringUtils.isNotBlank(regionEntity.getArea()) && !UNKNOWN_IP_INFO.equals(regionEntity.getArea())) {
            if (!region.isEmpty()) {
                region.append(" ");
            }
            region.append(regionEntity.getArea());
        }

        if (StringUtils.isNotBlank(regionEntity.getRegion()) && !UNKNOWN_IP_INFO.equals(regionEntity.getRegion())) {
            if (!region.isEmpty()) {
                region.append(" ");
            }
            region.append(regionEntity.getRegion());
        }

        if (StringUtils.isNotBlank(regionEntity.getISP()) && !UNKNOWN_IP_INFO.equals(regionEntity.getISP())) {
            if (!region.isEmpty()) {
                region.append(" ");
            }
            region.append(regionEntity.getISP());
        }

        return !region.isEmpty() ? region.toString() : UNKNOWN_IP_INFO;
    }

    /**
     * 根据IP地址获取结构化的地理位置信息 (IPEntity)
     * (优化版：增加了私网和CGN判断)
     *
     * @param ip IP地址
     * @return IPEntity对象，包含国家、省/州、城市和ISP信息。私网或CGN地址会返回特定标识。
     */
    public static IPEntity getRegionEntity(String ip) {
        IPEntity ipEntity = new IPEntity();
        // 将原始 IP 存入实体
        ipEntity.setIp(ip);

        // 1. 基础校验
        if (StringUtils.isBlank(ip)) {
            // 或设置所有字段为未知
            ipEntity.setRegion(UNKNOWN_IP_INFO);
            ipEntity.setISP(UNKNOWN_IP_INFO);
            return ipEntity;
        }

        // 2. 判断是否为私网、回环地址或 CGN 地址
        if (isPrivateOrCgnIp(ip)) {
            String info;
            if (ip.startsWith("127.")) {
                info = LOCALHOST_IO_INFO;
            } else if (ip.startsWith("100.") && ip.split("\\.").length == 4 &&
                    Integer.parseInt(ip.split("\\.")[1]) >= 64 &&
                    Integer.parseInt(ip.split("\\.")[1]) <= 127) {
                info = CGN_IP_INFO;
            } else {
                info = PRIVATE_IP_INFO;
            }
            ipEntity.setCountry(info);
            return ipEntity;
        }

        // 3. 调用 ip2region 查询公网 IP
        String regionResult = getRegionString(ip);

        if (regionResult != null) {
            // 解析 ip2region 返回的字符串: 国家|区域|省份|城市|ISP
            String[] parts = regionResult.split("\\|");
            int expectedLength = 5;
            if (parts.length >= expectedLength) {
                // ip2region 返回 "0" 表示未知
                ipEntity.setCountry("0".equals(parts[0]) ? UNKNOWN_IP_INFO : parts[0]);
                // parts[1] 通常是区域，国内为空，国外为大洲等，这里暂时不用

                // 省份
                String province = "0".equals(parts[2]) ? "" : parts[2];
                // 城市
                String city = "0".equals(parts[3]) ? "" : parts[3];
                // 单独省份
                ipEntity.setArea(province.isEmpty() ? UNKNOWN_IP_INFO : province);

                // 合并省份和城市作为详细区域
                StringBuilder regionBuilder = new StringBuilder();
                if (!province.isEmpty()) {
                    regionBuilder.append(province);
                }
                if (!city.isEmpty()) {
                    // 省市不同名时加空格
                    if (!regionBuilder.isEmpty() && !province.equals(city)) {
                        regionBuilder.append(" ");
                        // 只有城市信息
                    } else if (regionBuilder.isEmpty()) {
                        regionBuilder.append(city);
                    }
                    // 只有当省市不同名时，才在后面追加城市
                    if (!province.equals(city)) {
                        regionBuilder.append(city);
                    }
                }
                ipEntity.setRegion(regionBuilder.isEmpty() ? UNKNOWN_IP_INFO : regionBuilder.toString());

                ipEntity.setISP("0".equals(parts[4]) ? UNKNOWN_IP_INFO : parts[4]);

            } else {
                log.warn("Unexpected ip2region result format for IP {}: {}", ip, regionResult);
                // 将原始结果放入 region
                ipEntity.setRegion(regionResult);
                ipEntity.setISP(UNKNOWN_IP_INFO);
            }
        } else {
            // ip2region 查询无结果或出错
            log.warn("Could not determine region for public IP: {}", ip);
            ipEntity.setRegion(UNKNOWN_IP_INFO);
            ipEntity.setISP(UNKNOWN_IP_INFO);
        }

        return ipEntity;
    }

    /**
     * 初始化 ip2region
     * 从 ClassPath 加载 xdb 文件到临时文件进行初始化
     * ·
     */
    @PostConstruct
    public void init() {
        InputStream inputStream = null;
        try {
            // 从类路径加载资源文件
            inputStream = getClass().getResourceAsStream(DB_CLASSPATH_PATH);
            if (inputStream == null) {
                // 尝试从文件系统根目录加载（兼容某些部署场景）
                inputStream = Files.newInputStream(Path.of(DB_CLASSPATH_PATH));
            }


            // 将资源文件复制到临时文件，确保 Searcher 可以访问
            Path tempDbPath = Files.createTempFile("ip2region_", ".xdb");
            // 程序退出时删除临时文件
            tempDbPath.toFile().deleteOnExit();

            Files.copy(inputStream, tempDbPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied ip2region.xdb to temporary file: {}", tempDbPath);

            // 使用临时文件路径初始化 Searcher 对象
            searcher = Searcher.newWithFileOnly(tempDbPath.toString());
            log.info("Ip2region searcher initialized successfully using temp file.");

        } catch (Exception e) {
            log.error("Ip2region searcher initialization failed.", e);
            // 初始化失败，searcher 将保持为 null
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("Failed to close input stream for ip2region.xdb", e);
                }
            }
        }
    }
}
