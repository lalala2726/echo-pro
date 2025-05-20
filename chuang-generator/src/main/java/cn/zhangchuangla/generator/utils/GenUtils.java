package cn.zhangchuangla.generator.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:51
 */
public class GenUtils {


    /**
     * 将表名转换为符合Java类名规范的字符串
     * 转换规则：
     * 1. 下划线后紧跟的字符转为大写（首字母除外）
     * 2. 首字母自动转为大写
     * 3. 忽略其他特殊字符并全部转为小写处理
     *
     * @param tableName 数据库表名，如："user_info"
     * @return 对应的类名，如："UserInfo"
     */
    public static String convertClassName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return "";
        }

        StringBuilder className = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < tableName.length(); i++) {
            char c = tableName.charAt(i);

            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase || i == 0) {
                    className.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    className.append(Character.toLowerCase(c));
                }
            }
        }
        return className.toString();
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        int nameLength = packageName.length();
        return StringUtils.substring(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName) {
        int lastIndex = tableName.lastIndexOf("_");
        int nameLength = tableName.length();
        return StringUtils.substring(tableName, lastIndex + 1, nameLength);
    }
}
