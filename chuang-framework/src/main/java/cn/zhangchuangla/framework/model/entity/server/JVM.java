package cn.zhangchuangla.framework.model.entity.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * JVM信息
 *
 * @author Chuang
 * <p>
 * created on 2025/3/19 19:47
 */
@Data
@Schema(name = "JVM信息", description = "JVM信息")
public class JVM {

    /**
     * JVM版本
     */
    @Schema(description = "JVM版本")
    private String version;

    /**
     * JVM名称
     */
    @Schema(description = "JVM名称")
    private String name;

    /**
     * JVM路径
     */
    @Schema(description = "JVM路径")
    private String home;

    /**
     * JVM启动时间
     */
    @Schema(description = "JVM启动时间")
    private String startTime;

    /**
     * JVM运行时间
     */
    @Schema(description = "JVM运行时间")
    private String runTime;

    /**
     * JVM最大内存
     */
    @Schema(description = "JVM最大内存")
    private String maxMemory;

    /**
     * JVM总内存
     */
    @Schema(description = "JVM总内存")
    private String totalMemory;

    /**
     * JVM剩余内存
     */
    @Schema(description = "JVM剩余内存")
    private String freeMemory;

    /**
     * JVM使用内存
     */
    @Schema(description = "JVM使用内存")
    private String usedMemory;

    /**
     * JVM使用率
     */
    @Schema(description = "JVM使用率")
    private String usage;

}
