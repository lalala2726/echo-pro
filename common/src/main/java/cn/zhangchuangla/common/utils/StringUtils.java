package cn.zhangchuangla.common.utils;

/**
 * 字符串工具类，提供常见的字符串操作方法
 *
 * @author Chuang
 * created on 2025/1/12 11:23
 */
public class StringUtils {

    /**
     * 检查传入的字符串中是否有一个为null或空字符串
     *
     * @param strings 可变参数字符串
     * @return true 如果所有字符串都非空；false 如果任意一个字符串为null或空字符串
     */
    public static boolean isBlank(String... strings) {
        if (strings == null || strings.length == 0) {
            return true;
        }
        for (String str : strings) {
            if (str == null || str.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否非空（非null且非空字符串）
     *
     * @param str 字符串
     * @return true 如果非空，false 如果为null或空字符串
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 将字符串首字母转为大写
     *
     * @param str 输入字符串
     * @return 首字母大写的字符串，如果输入为null则返回null
     */
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 将字符串首字母转为小写
     *
     * @param str 输入字符串
     * @return 首字母小写的字符串，如果输入为null则返回null
     */
    public static String uncapitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 判断两个字符串是否相等（支持null安全）
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return true 如果相等，false 如果不相等
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    /**
     * 判断两个字符串是否忽略大小写相等（支持null安全）
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return true 如果相等（忽略大小写），false 如果不相等
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 判断字符串是否包含某个子字符串（支持null安全）
     *
     * @param str       主字符串
     * @param substring 子字符串
     * @return true 如果包含，false 如果不包含
     */
    public static boolean contains(String str, String substring) {
        if (str == null || substring == null) {
            return false;
        }
        return str.contains(substring);
    }

    /**
     * 去除字符串的前后空格，如果为null返回空字符串
     *
     * @param str 输入字符串
     * @return 去除空格后的字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 反转字符串
     *
     * @param str 输入字符串
     * @return 反转后的字符串，如果输入为null则返回null
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 重复字符串
     *
     * @param str   输入字符串
     * @param times 重复次数
     * @return 重复后的字符串，如果输入为null或次数小于1则返回空字符串
     */
    public static String repeat(String str, int times) {
        if (str == null || times < 1) {
            return "";
        }
        return str.repeat(times);
    }

    /**
     * 判断字符串是否以某个前缀开头（支持null安全）
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return true 如果以prefix开头，false 如果不以prefix开头或字符串为null
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * 判断字符串是否以某个后缀结尾（支持null安全）
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return true 如果以suffix结尾，false 如果不以suffix结尾或字符串为null
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }
}
