package cn.zhangchuangla.common.core.utils;

import cn.zhangchuangla.common.core.constant.Constants;

import java.util.Collection;

import static java.util.Objects.isNull;

/**
 * 字符串工具类，提供常见的字符串操作方法
 *
 * @author Chuang
 * created on 2025/1/12 11:23
 * //todo 逐步转移到Apache Commons Lang3
 */
public class StrUtils {

    /**
     * 空字符串
     */
    private static final String NULL_STR = "";


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
     * 如果字符串的最后一个字符是“/”，则替换为空字符串
     *
     * @param input 输入字符串
     * @return 处理后的字符串
     */
    public static String removeTrailingSlash(String input) {
        if (input != null && input.endsWith("/")) {
            return input.substring(0, input.length() - 1);
        }
        return input;
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
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
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

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULL_STR.equals(str.trim());
    }

    /**
     * 判断对象是否为空
     *
     * @param object 对象
     * @return false：为空 true：非空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 下划线转驼峰命名
     *
     * @param str 字符串
     * @return 驼峰命名字符串
     */
    public static String toCamelCase(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(str.length());
        // 标记是否需要转大写
        boolean toUpperCase = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '_') {
                toUpperCase = true;
            } else if (toUpperCase) {
                sb.append(Character.toUpperCase(c));
                toUpperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 判断集合是否为空
     *
     * @param coll 集合
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param coll 集合
     * @return true：非空 false：为空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 判断字符串是否为http(s)链接
     *
     * @param link 字符串
     * @return true：是 false：否
     */
    public static boolean isHttp(String link) {
        return org.apache.commons.lang3.StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 检查字符串是否有实际文本内容
     * <p>
     * 与 isEmpty 不同，该方法会检查字符串是否包含非空白字符。
     * 例如：" " 对于 isEmpty 返回 false，但对于 hasText 返回 false
     *
     * @param text 需要检查的字符串
     * @return true：如果字符串不为 null 且包含至少一个非空白字符，false：其他情况
     */
    public static boolean hasText(String text) {
        // 首先检查是否为 null
        if (text == null) {
            return false;
        }

        // 检查字符串是否为空
        if (text.isEmpty()) {
            return false;
        }

        // 检查是否全部为空白字符
        for (int i = 0; i < text.length(); i++) {
            // 如果有一个非空白字符，则认为有文本内容
            if (!Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }

        // 如果全部是空白字符，则认为没有文本内容
        return false;
    }

    /**
     * 检查字符串是否不包含实际文本内容
     * <p>
     * 是 hasText 方法的逻辑取反
     *
     * @param text 需要检查的字符串
     * @return true：如果字符串为 null 或者不包含非空白字符，false：其他情况
     */
    public static boolean hasNoText(String text) {
        return !hasText(text);
    }


}
