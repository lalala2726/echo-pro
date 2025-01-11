package cn.zhangchuangla.common.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类，提供常见的验证方法
 */
public class ValidationUtil {

    // 手机号正则
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    // 邮箱正则
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // 用户名正则 (5-16个字符，仅限字母、数字、下划线)
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{5,16}$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    // 密码正则 (6-16个字符，必须包含字母和数字)
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,16}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * 验证手机号是否合法
     *
     * @param phone 手机号
     * @return true 合法，false 不合法
     */
    public static boolean isPhoneValid(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证邮箱是否合法
     *
     * @param email 邮箱
     * @return true 合法，false 不合法
     */
    public static boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证用户名是否合法
     *
     * @param username 用户名
     * @return true 合法，false 不合法
     */
    public static boolean isUsernameValid(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 验证密码是否合法
     *
     * @param password 密码
     * @return true 合法，false 不合法
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
