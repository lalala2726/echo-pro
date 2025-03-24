package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.exception.ProfileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Slf4j
public class PathUtils {

    //  Windows 路径正则（D:\ 开头 或 C:\ 开头）
    private static final Pattern WINDOWS_PATH_PATTERN = Pattern.compile("^[a-zA-Z]:\\\\.*");

    //  Linux 路径正则（/ 开头）
    private static final Pattern LINUX_PATH_PATTERN = Pattern.compile("^/.*");

    /**
     * 判断是否是 Linux 路径
     *
     * @param path 文件路径
     * @return true/false
     */
    public static boolean isLinuxPath(String path) {
        return StringUtils.isNotBlank(path) && LINUX_PATH_PATTERN.matcher(path).matches();
    }

    /**
     * 判断是否是 Windows 路径
     *
     * @param path 文件路径
     * @return true/false
     */
    public static boolean isWindowsPath(String path) {
        return StringUtils.isNotBlank(path) && WINDOWS_PATH_PATTERN.matcher(path).matches();
    }

    /**
     * 处理 Windows 路径
     *
     * @param path 文件路径
     * @return 处理后的路径
     */
    public static String processWindowsPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new ProfileException("路径不能为空！");
        }
        // 检查路径是否包含转义字符
        if (path.contains("\\\\") || path.contains("/")) {
            log.info("路径包含转义字符，无需修改: {}", path);
            return path;
        }
        // 自动补全转义字符
        String processedPath = path.replace("\\", "\\\\");
        log.info("路径未携带转义字符，已自动处理为: {}", processedPath);
        return processedPath;
    }
}
