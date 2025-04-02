package cn.zhangchuangla.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 14:49
 */
public class MD5Utils {

    /**
     * MD5加密字符串
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密出错", e);
        }
    }

    /**
     * MD5加密字符串（加盐）
     *
     * @param str  待加密的字符串
     * @param salt 盐值
     * @return 加密后的字符串
     */
    public static String encrypt(String str, String salt) {
        return encrypt(str + salt);
    }

    /**
     * 将byte数组转换为16进制字符串
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    /**
     * 校验密码是否正确
     *
     * @param password 明文密码
     * @param md5      密文密码
     * @return 校验结果
     */
    public static boolean verify(String password, String md5) {
        return encrypt(password).equals(md5);
    }

    /**
     * 校验密码是否正确（加盐）
     *
     * @param password 明文密码
     * @param md5      密文密码
     * @param salt     盐值
     * @return 校验结果
     */
    public static boolean verify(String password, String md5, String salt) {
        return encrypt(password, salt).equals(md5);
    }
}
