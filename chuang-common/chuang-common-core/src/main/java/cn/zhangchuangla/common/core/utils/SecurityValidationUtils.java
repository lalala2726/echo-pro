package cn.zhangchuangla.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * 安全验证工具类
 * 提供密码强度检查、JWT密钥验证、安全随机数生成等功能
 *
 * @author Chuang
 */
@Slf4j
public final class SecurityValidationUtils {

    private SecurityValidationUtils() {
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // 密码强度正则表达式：至少8位，包含大小写字母、数字
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    // 非常强的密码：至少10位，包含大小写字母、数字、特殊字符
    private static final Pattern VERY_STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$"
    );

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 密码强度等级 (WEAK, MEDIUM, STRONG, VERY_STRONG)
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (StringUtils.isBlank(password)) {
            return PasswordStrength.WEAK;
        }

        if (password.length() < 6) {
            return PasswordStrength.WEAK;
        }

        if (VERY_STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.VERY_STRONG;
        }

        if (STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.STRONG;
        }

        if (password.length() >= 8) {
            return PasswordStrength.MEDIUM;
        }

        return PasswordStrength.WEAK;
    }

    /**
     * 验证JWT密钥强度
     *
     * @param secret JWT密钥
     * @return 是否符合安全要求
     */
    public static boolean isSecureJwtSecret(String secret) {
        if (StringUtils.isBlank(secret)) {
            log.warn("JWT密钥为空");
            return false;
        }

        // JWT密钥至少需要256位（32字节）
        if (secret.length() < 32) {
            log.warn("JWT密钥长度不足32位：{}", secret.length());
            return false;
        }

        // 检查是否为简单重复模式
        if (isRepeatingPattern(secret)) {
            log.warn("JWT密钥包含重复模式，不够安全");
            return false;
        }

        // 检查熵值（简单检查）
        if (calculateEntropy(secret) < 4.0) {
            log.warn("JWT密钥熵值过低，建议使用更复杂的密钥");
            return false;
        }

        return true;
    }

    /**
     * 生成安全的JWT密钥
     *
     * @param length 密钥长度（建议至少32位）
     * @return 安全的JWT密钥
     */
    public static String generateSecureJwtSecret(int length) {
        if (length < 32) {
            length = 32; // 最小长度32位
        }

        StringBuilder secret = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=";
        
        for (int i = 0; i < length; i++) {
            secret.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }

        return secret.toString();
    }

    /**
     * 生成安全的随机密码
     *
     * @param length 密码长度
     * @param includeSpecialChars 是否包含特殊字符
     * @return 随机密码
     */
    public static String generateSecurePassword(int length, boolean includeSpecialChars) {
        if (length < 8) {
            length = 8; // 最小长度8位
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (includeSpecialChars) {
            chars += "!@#$%^&*()_+-=[]{}|;:,.<>?";
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * 验证是否为常见弱密码
     *
     * @param password 密码
     * @return 是否为弱密码
     */
    public static boolean isCommonWeakPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return true;
        }

        String[] commonPasswords = {
            "123456", "password", "123456789", "12345678", "12345", 
            "1234567", "1234567890", "qwerty", "abc123", "admin",
            "admin123", "password123", "123123", "root", "toor"
        };

        String lowerPassword = password.toLowerCase();
        for (String weak : commonPasswords) {
            if (lowerPassword.equals(weak)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查字符串是否包含重复模式
     *
     * @param input 输入字符串
     * @return 是否包含重复模式
     */
    private static boolean isRepeatingPattern(String input) {
        if (StringUtils.isBlank(input) || input.length() < 4) {
            return false;
        }

        // 检查简单重复（如 "aaaa", "1111"）
        char firstChar = input.charAt(0);
        boolean allSame = true;
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) != firstChar) {
                allSame = false;
                break;
            }
        }
        if (allSame) {
            return true;
        }

        // 检查短重复模式（如 "abcabcabc"）
        for (int patternLength = 2; patternLength <= input.length() / 3; patternLength++) {
            String pattern = input.substring(0, patternLength);
            boolean isRepeating = true;
            
            for (int i = patternLength; i < input.length(); i += patternLength) {
                int endIndex = Math.min(i + patternLength, input.length());
                String segment = input.substring(i, endIndex);
                
                if (!pattern.startsWith(segment)) {
                    isRepeating = false;
                    break;
                }
            }
            
            if (isRepeating) {
                return true;
            }
        }

        return false;
    }

    /**
     * 计算字符串的熵值（Shannon熵）
     *
     * @param input 输入字符串
     * @return 熵值
     */
    private static double calculateEntropy(String input) {
        if (StringUtils.isBlank(input)) {
            return 0.0;
        }

        int[] charCounts = new int[256];
        for (char c : input.toCharArray()) {
            charCounts[c]++;
        }

        double entropy = 0.0;
        int length = input.length();
        
        for (int count : charCounts) {
            if (count > 0) {
                double probability = (double) count / length;
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }

        return entropy;
    }

    /**
     * 密码强度枚举
     */
    public enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中等"),
        STRONG("强"),
        VERY_STRONG("很强");

        private final String description;

        PasswordStrength(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}