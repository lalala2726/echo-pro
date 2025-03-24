package cn.zhangchuangla.common.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author zhangchuang
 * Created on 2025/3/22 14:39
 */
public class URLUtils {

    /**
     * 从URL中提取文件扩展名
     *
     * @param url 文件URL
     * @return 文件扩展名（带点，如 .jpg）
     */
    public static String extractExtensionFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }
        // 移除URL参数
        String cleanUrl = url;
        int queryIndex = cleanUrl.indexOf('?');
        if (queryIndex > 0) {
            cleanUrl = cleanUrl.substring(0, queryIndex);
        }
        // 获取最后一个点之后的内容作为扩展名
        int lastDotIndex = cleanUrl.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return cleanUrl.substring(lastDotIndex);
        }

        return "";
    }

    /**
     * 从URL中提取文件路径
     *
     * @param url 文件URL
     * @return 文件路径
     */
    public static String extractPathFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }

        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();

            // 如果路径为空，则返回空字符串
            if (StringUtils.isBlank(path)) {
                return "";
            }

            // 删除开头的斜杠
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            return path;
        } catch (MalformedURLException e) {
            // 如果URL格式不正确，尝试简单提取
            int protocolIndex = url.indexOf("://");
            if (protocolIndex > 0) {
                String remaining = url.substring(protocolIndex + 3);
                int pathStartIndex = remaining.indexOf('/');
                if (pathStartIndex >= 0) {
                    return remaining.substring(pathStartIndex + 1);
                }
            }

            return "";
        }
    }


    /**
     * 从URL中提取文件名
     *
     * @param url 文件URL
     * @return 文件名
     */
    public static String extractFilenameFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }
        // 移除URL参数
        String cleanUrl = url;
        int queryIndex = cleanUrl.indexOf('?');
        if (queryIndex > 0) {
            cleanUrl = cleanUrl.substring(0, queryIndex);
        }

        // 获取最后一个斜杠之后的内容作为文件名
        int lastSlashIndex = cleanUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < cleanUrl.length() - 1) {
            return cleanUrl.substring(lastSlashIndex + 1);
        }

        return cleanUrl;
    }
}
