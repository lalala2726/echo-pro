package cn.zhangchuangla.system.monitor.util;

import java.text.DecimalFormat;

/**
 * 监控工具类
 *
 * @author Chuang
 * created on 2025/7/28
 */
public class MonitorUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final String[] BYTE_UNITS = {"B", "KB", "MB", "GB", "TB", "PB"};
    private static final String[] FREQUENCY_UNITS = {"Hz", "KHz", "MHz", "GHz", "THz"};

    /**
     * 格式化字节大小
     *
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    public static String formatBytes(long bytes) {
        if (bytes <= 0) {
            return "0 B";
        }

        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < BYTE_UNITS.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return DECIMAL_FORMAT.format(size) + " " + BYTE_UNITS[unitIndex];
    }

    /**
     * 格式化频率
     *
     * @param hertz 频率（Hz）
     * @return 格式化后的字符串
     */
    public static String formatFrequency(long hertz) {
        if (hertz <= 0) {
            return "0 Hz";
        }

        int unitIndex = 0;
        double frequency = hertz;

        while (frequency >= 1000 && unitIndex < FREQUENCY_UNITS.length - 1) {
            frequency /= 1000;
            unitIndex++;
        }

        return DECIMAL_FORMAT.format(frequency) + " " + FREQUENCY_UNITS[unitIndex];
    }

    /**
     * 格式化百分比
     *
     * @param value 数值
     * @return 格式化后的百分比字符串
     */
    public static String formatPercentage(double value) {
        return DECIMAL_FORMAT.format(value) + "%";
    }

    /**
     * 格式化时间（毫秒转换为可读格式）
     *
     * @param milliseconds 毫秒数
     * @return 格式化后的时间字符串
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        }

        long seconds = milliseconds / 1000;
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        if (minutes < 60) {
            return minutes + "m " + seconds + "s";
        }

        long hours = minutes / 60;
        minutes = minutes % 60;
        if (hours < 24) {
            return hours + "h " + minutes + "m " + seconds + "s";
        }

        long days = hours / 24;
        hours = hours % 24;
        return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
    }

    /**
     * 格式化运行时间（秒转换为可读格式）
     *
     * @param seconds 秒数
     * @return 格式化后的时间字符串
     */
    public static String formatUptime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        if (minutes < 60) {
            return minutes + "m " + seconds + "s";
        }

        long hours = minutes / 60;
        minutes = minutes % 60;
        if (hours < 24) {
            return hours + "h " + minutes + "m";
        }

        long days = hours / 24;
        hours = hours % 24;
        return days + "d " + hours + "h " + minutes + "m";
    }

    /**
     * 计算使用率百分比
     *
     * @param used  已使用量
     * @param total 总量
     * @return 使用率百分比
     */
    public static double calculateUsagePercentage(long used, long total) {
        if (total <= 0) {
            return 0.0;
        }
        return Math.round((double) used / total * 100 * 100.0) / 100.0;
    }

    /**
     * 计算使用率百分比
     *
     * @param used  已使用量
     * @param total 总量
     * @return 使用率百分比
     */
    public static double calculateUsagePercentage(double used, double total) {
        if (total <= 0) {
            return 0.0;
        }
        return Math.round(used / total * 100 * 100.0) / 100.0;
    }

    /**
     * 判断是否为健康状态
     *
     * @param usage     使用率
     * @param threshold 阈值
     * @return 是否健康
     */
    public static boolean isHealthy(double usage, double threshold) {
        return usage < threshold;
    }

    /**
     * 获取健康状态字符串
     *
     * @param usage     使用率
     * @param threshold 阈值
     * @return 健康状态字符串
     */
    public static String getHealthStatus(double usage, double threshold) {
        return isHealthy(usage, threshold) ? "HEALTHY" : "WARNING";
    }

    /**
     * 格式化数字（保留两位小数）
     *
     * @param value 数值
     * @return 格式化后的字符串
     */
    public static String formatNumber(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    /**
     * 安全地解析长整型
     *
     * @param value        字符串值
     * @param defaultValue 默认值
     * @return 解析后的长整型值
     */
    public static long safeParseLong(String value, long defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全地解析整型
     *
     * @param value        字符串值
     * @param defaultValue 默认值
     * @return 解析后的整型值
     */
    public static int safeParseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全地解析双精度浮点型
     *
     * @param value        字符串值
     * @param defaultValue 默认值
     * @return 解析后的双精度浮点型值
     */
    public static double safeParseDouble(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取状态颜色（用于前端显示）
     *
     * @param usage            使用率
     * @param warningThreshold 警告阈值
     * @param dangerThreshold  危险阈值
     * @return 状态颜色
     */
    public static String getStatusColor(double usage, double warningThreshold, double dangerThreshold) {
        if (usage >= dangerThreshold) {
            return "danger";
        } else if (usage >= warningThreshold) {
            return "warning";
        } else {
            return "success";
        }
    }

    /**
     * 获取状态等级
     *
     * @param usage            使用率
     * @param warningThreshold 警告阈值
     * @param dangerThreshold  危险阈值
     * @return 状态等级
     */
    public static String getStatusLevel(double usage, double warningThreshold, double dangerThreshold) {
        if (usage >= dangerThreshold) {
            return "CRITICAL";
        } else if (usage >= warningThreshold) {
            return "WARNING";
        } else {
            return "NORMAL";
        }
    }
}
