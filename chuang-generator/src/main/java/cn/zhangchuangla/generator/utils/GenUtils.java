package cn.zhangchuangla.generator.utils;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:51
 */
public class GenUtils {

    /**
     * 需要排除的字段（不作为查询条件）
     */
    private static final Set<String> EXCLUDE_FIELDS = new HashSet<>(Arrays.asList(
            "id", "create_by", "create_time", "update_by", "update_time", "del_flag", "remark"
    ));

    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param name 下划线命名的字符串
     * @return 驼峰命名的字符串
     */
    public static String toCamelCase(String name) {
        if (StrUtil.isBlank(name)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] words = name.toLowerCase().split("_");

        // 第一个单词小写
        result.append(words[0]);

        // 其他单词首字母大写
        for (int i = 1; i < words.length; i++) {
            if (words[i].length() > 0) {
                result.append(Character.toUpperCase(words[i].charAt(0)))
                        .append(words[i].substring(1));
            }
        }

        return result.toString();
    }

    /**
     * 将表名转换为类名（首字母大写的驼峰命名）
     *
     * @param tableName 表名
     * @return 类名
     */
    public static String convertClassName(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] words = tableName.toLowerCase().split("_");

        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return result.toString();
    }

    /**
     * 根据数据库字段类型获取Java类型
     *
     * @param columnType 数据库字段类型
     * @return Java类型
     */
    public static String getJavaType(String columnType) {
        if (StrUtil.isBlank(columnType)) {
            return "String";
        }

        String type = columnType.toLowerCase();

        if (type.contains("char") || type.contains("text") || type.contains("enum")) {
            return "String";
        } else if (type.contains("bigint")) {
            return "Long";
        } else if (type.contains("int")) {
            return "Integer";
        } else if (type.contains("float")) {
            return "Float";
        } else if (type.contains("double")) {
            return "Double";
        } else if (type.contains("decimal")) {
            return "BigDecimal";
        } else if (type.contains("date") || type.contains("time")) {
            return "Date";
        } else if (type.contains("blob")) {
            return "byte[]";
        } else if (type.contains("boolean") || type.contains("bit")) {
            return "Boolean";
        }

        return "String";
    }

    /**
     * 根据数据库字段类型获取表单显示类型
     *
     * @param columnType 数据库字段类型
     * @return 表单显示类型
     */
    public static String getHtmlType(String columnType) {
        if (StrUtil.isBlank(columnType)) {
            return "input";
        }

        String type = columnType.toLowerCase();

        if (type.contains("text")) {
            return "textarea";
        } else if (type.contains("date") || type.contains("time")) {
            return "datetime";
        } else if (type.contains("enum") || type.contains("set")) {
            return "select";
        } else if (type.contains("int") || type.contains("float") || type.contains("double") || type.contains("decimal")) {
            return "input";
        }

        return "input";
    }

    /**
     * 获取包名的最后一段作为模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        if (StrUtil.isBlank(packageName)) {
            return "";
        }

        int lastIndex = packageName.lastIndexOf(".");
        return lastIndex > 0 ? packageName.substring(lastIndex + 1) : packageName;
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return "";
        }

        // 去除表前缀
        String businessName = tableName;

        // 如果表名包含下划线，取最后一段作为业务名
        int lastIndex = businessName.lastIndexOf("_");
        if (lastIndex > 0) {
            businessName = businessName.substring(lastIndex + 1);
        }

        return businessName;
    }

    /**
     * 判断字段是否为需要排除的字段
     *
     * @param fieldName 字段名
     * @return 是否排除
     */
    public static boolean isExcludeField(String fieldName) {
        return EXCLUDE_FIELDS.contains(fieldName.toLowerCase());
    }
}
