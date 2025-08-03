package cn.zhangchuangla.common.core.utils;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * 验证码工具类，用于生成随机验证码，包括纯数字、纯字母、数字和字母混合，并且支持自定义字符集与长度。
 * <p>
 * 用 {@link SecureRandom} 保证随机性质量，适合需要防猜测的场景（非图像验证码的随机字符串）。
 * <p>
 * 示例：
 * <pre>
 *   String num = CaptchaUtils.randomNumeric(); // 纯数字，6 位
 *   String alpha = CaptchaUtils.randomAlphabetic(); // 纯小写字母，6 位
 *   String mix = CaptchaUtils.randomAlphanumeric(); // 数字+小写字母，6 位
 *   String custom = CaptchaUtils.randomCustom("ABC123"); // 从指定字符集中 6 位
 * </pre>
 * <p>
 * 也可以指定长度 / 大小写：
 * <pre>
 *   String alphaCase = CaptchaUtils.randomAlphabetic(5, true); // 大小写混合
 *   String mixCase = CaptchaUtils.randomAlphanumeric(8, false); // 数字 + 小写
 * </pre>
 *
 * @author Chuang
 * created on 2025/8/3 23:56
 */
public final class CaptchaUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    // 默认字符集
    private static final String DIGITS = "0123456789";
    private static final String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = LOWER_ALPHA + UPPER_ALPHA;

    private static final String DEFAULT_ALPHANUMERIC = DIGITS + LOWER_ALPHA;

    // 易混淆字符（可选去除）
    private static final String CONFUSING_CHARS = "0O1lI";

    private static final int DEFAULT_LENGTH = 6;

    private CaptchaUtils() {
        // 防止实例化
    }

    // ---------------------- 无参默认方法，6 位 ----------------------

    /**
     * 默认 6 位纯数字验证码。
     */
    public static String randomNumeric() {
        return randomNumeric(DEFAULT_LENGTH);
    }

    /**
     * 默认 6 位纯小写字母验证码。
     */
    public static String randomAlphabetic() {
        return randomAlphabetic(DEFAULT_LENGTH, false);
    }

    /**
     * 默认 6 位数字+小写字母混合验证码。
     */
    public static String randomAlphanumeric() {
        return randomAlphanumeric(DEFAULT_LENGTH, false);
    }

    /**
     * 默认从给定字符集取 6 位。
     */
    public static String randomCustom(String charset) {
        return randomCustom(charset, DEFAULT_LENGTH);
    }


    /**
     * 生成指定长度的纯数字验证码。
     *
     * @param length 长度，必须大于 0
     * @return 指定长度的数字字符串
     */
    public static String randomNumeric(int length) {
        return randomFromCharset(length, DIGITS);
    }

    /**
     * 生成指定长度的纯字母验证码。
     *
     * @param length      长度，必须大于 0
     * @param includeCase 是否包含大写字母（true：大小写混合，false：仅小写）
     * @return 指定长度的字母字符串
     */
    public static String randomAlphabetic(int length, boolean includeCase) {
        String charset = includeCase ? ALPHA : LOWER_ALPHA;
        return randomFromCharset(length, charset);
    }

    /**
     * 生成指定长度的字母+数字混合验证码。
     *
     * @param length      长度，必须大于 0
     * @param includeCase 是否包含大写字母（true：大小写混合，false：仅小写字母 + 数字）
     * @return 指定长度的字母数字混合字符串
     */
    public static String randomAlphanumeric(int length, boolean includeCase) {
        String letters = includeCase ? ALPHA : LOWER_ALPHA;
        String charset = DIGITS + letters;
        return randomFromCharset(length, charset);
    }

    /**
     * 从自定义字符集中生成指定长度的验证码。
     *
     * @param charset 字符集，不能为空且长度必须 >=1
     * @param length  长度，必须大于 0
     * @return 指定长度的随机字符串
     */
    public static String randomCustom(String charset, int length) {
        Objects.requireNonNull(charset, "charset must not be null");
        if (charset.isEmpty()) {
            throw new IllegalArgumentException("charset must contain at least one character");
        }
        return randomFromCharset(length, charset);
    }

    /**
     * 生成验证码并排除一些易混淆字符（比如 0/O, 1/l/I）。
     * 只在原始字符集中去除这些字符后再抽取。
     *
     * @param baseCharset     原始字符集
     * @param length          长度
     * @param removeConfusing 是否移除混淆字符
     * @return 随机字符串
     */
    public static String randomWithOptionallyFilteredChars(String baseCharset, int length, boolean removeConfusing) {
        String charset = baseCharset;
        if (removeConfusing) {
            for (char c : CONFUSING_CHARS.toCharArray()) {
                charset = charset.replace(String.valueOf(c), "");
            }
            if (charset.isEmpty()) {
                throw new IllegalArgumentException("After removing confusing chars, charset is empty");
            }
        }
        return randomFromCharset(length, charset);
    }

    // ---------------------- 内部通用逻辑 ----------------------

    private static String randomFromCharset(int length, String charset) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        char[] chars = charset.toCharArray();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(chars.length);
            sb.append(chars[idx]);
        }
        return sb.toString();
    }
}
