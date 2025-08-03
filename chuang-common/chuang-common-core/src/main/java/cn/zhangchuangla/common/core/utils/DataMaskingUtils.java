package cn.zhangchuangla.common.core.utils;

import cn.zhangchuangla.common.core.annotation.DataMasking;
import cn.zhangchuangla.common.core.enums.MaskingType;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 数据脱敏工具类
 *
 * @author Chuang
 */
public class DataMaskingUtils {

    /**
     * 执行数据脱敏
     *
     * @param data        原始数据
     * @param dataMasking 脱敏注解
     * @return 脱敏后的数据
     */
    public static String mask(String data, DataMasking dataMasking) {
        if (StringUtils.isBlank(data)) {
            return data;
        }

        MaskingType type = dataMasking.type();

        // 如果是自定义类型，使用注解中的正则表达式
        if (type == MaskingType.CUSTOM) {
            return maskWithCustomRegex(data, dataMasking);
        }

        // 使用预定义的脱敏规则
        return maskWithPredefinedType(data, type, dataMasking);
    }

    /**
     * 使用自定义正则表达式进行脱敏
     */
    private static String maskWithCustomRegex(String data, DataMasking dataMasking) {
        String regex = dataMasking.regex();
        String replacement = dataMasking.replacement();

        if (StringUtils.isBlank(regex)) {
            // 如果没有指定正则表达式，使用前后保留字符数的方式
            return maskWithKeepChars(data, dataMasking.prefixKeep(), dataMasking.suffixKeep(),
                    dataMasking.maskChar(), dataMasking.preserveLength(), dataMasking.maskLength());
        }

        try {
            return data.replaceAll(regex, replacement);
        } catch (Exception e) {
            // 如果正则表达式有问题，返回原始数据
            return data;
        }
    }

    /**
     * 使用预定义的脱敏类型进行脱敏
     */
    private static String maskWithPredefinedType(String data, MaskingType type, DataMasking dataMasking) {
        String regex = type.getRegex();
        String replacement = type.getReplacement();

        // 如果指定了自定义脱敏字符，替换默认的*号
        if (!"*".equals(dataMasking.maskChar())) {
            replacement = replacement.replaceAll("\\*+", generateMaskChars(4, dataMasking.maskChar()));
        }

        try {
            // 检查数据是否匹配正则表达式
            if (Pattern.matches(regex, data)) {
                return data.replaceAll(regex, replacement);
            } else {
                // 如果不匹配，尝试使用通用的前后保留字符数方式
                return maskWithDefaultKeepChars(data, type, dataMasking.maskChar(),
                        dataMasking.preserveLength(), dataMasking.maskLength());
            }
        } catch (Exception e) {
            // 如果出现异常，返回原始数据
            return data;
        }
    }

    /**
     * 根据前后保留字符数进行脱敏
     */
    private static String maskWithKeepChars(String data, int prefixKeep, int suffixKeep, String maskChar,
                                            boolean preserveLength, int specifiedMaskLength) {
        int length = data.length();

        // 如果没有指定保留字符数，使用默认规则
        if (prefixKeep == -1 && suffixKeep == -1) {
            // 默认保留前1/3和后1/3
            prefixKeep = Math.max(1, length / 3);
            suffixKeep = Math.max(1, length / 3);
        } else if (prefixKeep == -1) {
            prefixKeep = Math.max(0, length - suffixKeep - 2);
        } else if (suffixKeep == -1) {
            suffixKeep = Math.max(0, length - prefixKeep - 2);
        }

        // 确保前后保留的字符数不超过总长度
        if (prefixKeep + suffixKeep >= length) {
            prefixKeep = Math.max(1, length / 2);
            suffixKeep = Math.max(1, length - prefixKeep - 1);
        }

        String prefix = data.substring(0, Math.min(prefixKeep, length));
        String suffix = suffixKeep > 0 && length > prefixKeep + suffixKeep
                ? data.substring(length - suffixKeep)
                : "";

        // 计算脱敏字符长度
        int maskLength;
        if (preserveLength) {
            // 保留原长度
            maskLength = Math.max(1, length - prefix.length() - suffix.length());
        } else {
            // 使用指定长度或默认长度
            if (specifiedMaskLength > 0) {
                maskLength = specifiedMaskLength;
            } else {
                // 默认使用4个脱敏字符
                maskLength = 4;
            }
        }

        String mask = generateMaskChars(maskLength, maskChar);
        return prefix + mask + suffix;
    }

    /**
     * 根据脱敏类型使用默认的保留字符数
     */
    private static String maskWithDefaultKeepChars(String data, MaskingType type, String maskChar,
                                                   boolean preserveLength, int specifiedMaskLength) {
        switch (type) {
            case MOBILE_PHONE:
                return maskWithKeepChars(data, 3, 4, maskChar, preserveLength, specifiedMaskLength);
            case ID_CARD:
                return maskWithKeepChars(data, 6, 4, maskChar, preserveLength, specifiedMaskLength);
            case EMAIL:
                int atIndex = data.indexOf("@");
                if (atIndex > 0) {
                    String prefix = data.substring(0, 1);
                    String suffix = data.substring(atIndex);
                    int maskLength = preserveLength ? Math.max(1, atIndex - 1) :
                            (specifiedMaskLength > 0 ? specifiedMaskLength : 3);
                    return prefix + generateMaskChars(maskLength, maskChar) + suffix;
                }
                return maskWithKeepChars(data, 1, 0, maskChar, preserveLength, specifiedMaskLength);
            case NAME:
                return maskWithKeepChars(data, 1, 0, maskChar, preserveLength, specifiedMaskLength);
            case BANK_CARD, FIXED_PHONE:
                return maskWithKeepChars(data, 4, 4, maskChar, preserveLength, specifiedMaskLength);
            case ADDRESS:
                return maskWithKeepChars(data, 3, 2, maskChar, preserveLength, specifiedMaskLength);
            case PASSWORD:
                int passwordMaskLength = preserveLength ? data.length() :
                        (specifiedMaskLength > 0 ? specifiedMaskLength : 6);
                return generateMaskChars(passwordMaskLength, maskChar);
            case SECRET_KEY:
                return maskWithKeepChars(data, 3, 3, maskChar, preserveLength, specifiedMaskLength);
            default:
                return maskWithKeepChars(data, 1, 1, maskChar, preserveLength, specifiedMaskLength);
        }
    }

    /**
     * 生成指定数量的脱敏字符
     */
    private static String generateMaskChars(int count, String maskChar) {
        return String.valueOf(maskChar).repeat(Math.max(0, count));
    }
}
