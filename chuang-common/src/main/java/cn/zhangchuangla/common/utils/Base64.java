package cn.zhangchuangla.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Base64 编码解码工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 14:49
 */
public class Base64 {

    private static final Encoder encoder = java.util.Base64.getEncoder();
    private static final Decoder decoder = java.util.Base64.getDecoder();

    /**
     * Base64 编码
     *
     * @param bytes 字节数组
     * @return 编码后的字符串
     */
    public static String encode(byte[] bytes) {
        return encoder.encodeToString(bytes);
    }

    /**
     * Base64 编码
     *
     * @param str 需要编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String str) {
        return encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码为字节数组
     *
     * @param str Base64 编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decode(String str) {
        return decoder.decode(str);
    }

    /**
     * Base64 解码为字符串
     *
     * @param str Base64 编码的字符串
     * @return 解码后的字符串
     */
    public static String decodeToString(String str) {
        return new String(decoder.decode(str), StandardCharsets.UTF_8);
    }

    /**
     * URL安全的Base64编码
     *
     * @param bytes 字节数组
     * @return 编码后的字符串
     */
    public static String encodeUrlSafe(byte[] bytes) {
        return java.util.Base64.getUrlEncoder().encodeToString(bytes);
    }

    /**
     * URL安全的Base64解码
     *
     * @param str Base64 编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decodeUrlSafe(String str) {
        return java.util.Base64.getUrlDecoder().decode(str);
    }
}
