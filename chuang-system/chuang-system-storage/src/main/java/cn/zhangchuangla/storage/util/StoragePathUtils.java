package cn.zhangchuangla.storage.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

/**
 * 存储路径生成和操作工具类
 *
 * @author Chuang
 */
public class StoragePathUtils {

    private static final String URL_SEPARATOR = "/";

    /**
     * 根据日期和原始文件名生成包含子目录的唯一文件路径
     *
     * @param subPath        可选的基础子路径（例如："user-uploads", "images"）。可以为 null 或空字符串
     * @param uniqueFileName 文件的唯一名称（例如：带时间戳/UUID）
     * @return 相对路径字符串，例如："user-uploads/2023/05/17/uniqueFileName.jpg" 或 "2023/05/17/uniqueFileName.jpg"
     */
    public static String generatePath(String subPath, String uniqueFileName) {
        java.time.LocalDate today = java.time.LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        StringBuilder pathBuilder = new StringBuilder();
        if (StringUtils.hasText(subPath)) {
            pathBuilder.append(sanitizePath(subPath)).append(URL_SEPARATOR);
        }
        pathBuilder.append(year).append(URL_SEPARATOR);
        pathBuilder.append(month).append(URL_SEPARATOR);
        pathBuilder.append(day).append(URL_SEPARATOR);
        pathBuilder.append(sanitizePath(uniqueFileName));

        return pathBuilder.toString();
    }

    /**
     * 生成原始图片的存储路径
     *
     * @param uniqueFileName 文件的唯一名称
     * @return 路径示例："images/original/2023/05/17/uniqueFileName.jpg"
     */
    public static String generateOriginalImagePath(String uniqueFileName) {
        return generatePath("images/original", uniqueFileName);
    }

    /**
     * 生成压缩图/缩略图的存储路径
     *
     * @param uniqueFileName 文件的唯一名称
     * @return 路径示例："images/thumbnail/2023/05/17/uniqueFileName.jpg"
     */
    public static String generateThumbnailImagePath(String uniqueFileName) {
        return generatePath("images/thumbnail", uniqueFileName);
    }


    /**
     * 对路径片段进行安全处理以防止目录遍历问题
     * 移除首尾的斜杠并规范化路径分隔符
     *
     * @param pathSegment 需要处理的路径片段
     * @return 处理后的路径片段
     */
    private static String sanitizePath(String pathSegment) {
        if (!StringUtils.hasText(pathSegment)) {
            return "";
        }
        // 将路径分隔符统一转换为正斜杠
        String normalized = FilenameUtils.normalizeNoEndSeparator(pathSegment.replace("\\", URL_SEPARATOR), true);
        // 移除开头的斜杠以避免在相对路径中被解释为绝对路径
        if (normalized.startsWith(URL_SEPARATOR)) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    /**
     * 将基础URL/域名与相对路径拼接成完整URL
     *
     * @param baseOrDomain   基础URL或域名（例如："https://cdn.example.com", "/resources"）
     * @param relativePath   文件的相对路径（例如："images/pic.jpg"）
     * @return 完整的URL
     */
    public static String concatUrl(String baseOrDomain, String relativePath) {
        String sanitizedBase = baseOrDomain != null ? baseOrDomain.trim() : "";
        String sanitizedRelativePath = relativePath != null ? relativePath.trim() : "";

        // 移除base末尾的斜杠
        if (sanitizedBase.endsWith(URL_SEPARATOR)) {
            sanitizedBase = sanitizedBase.substring(0, sanitizedBase.length() - 1);
        }
        // 移除相对路径开头的斜杠
        if (sanitizedRelativePath.startsWith(URL_SEPARATOR)) {
            sanitizedRelativePath = sanitizedRelativePath.substring(1);
        }
        return sanitizedBase + URL_SEPARATOR + sanitizedRelativePath;
    }
}
