package cn.zhangchuangla.infrastructure.model.entity.server;

import lombok.Data;

/**
 * JVM信息
 *
 * @author Chuang
 * <p>
 * created on 2025/3/19 19:47
 */
@Data
public class JVM {

    /**
     * JVM版本
     */
    private String version;

    /**
     * JVM名称
     */
    private String name;

    /**
     * JVM路径
     */
    private String home;

    /**
     * JVM启动时间
     */
    private String startTime;

    /**
     * JVM运行时间
     */
    private String runTime;

    /**
     * JVM最大内存
     */
    private String maxMemory;

    /**
     * JVM总内存
     */
    private String totalMemory;

    /**
     * JVM剩余内存
     */
    private String freeMemory;

    /**
     * JVM使用内存
     */
    private String usedMemory;

    /**
     * JVM使用率
     */
    private String usage;

}
