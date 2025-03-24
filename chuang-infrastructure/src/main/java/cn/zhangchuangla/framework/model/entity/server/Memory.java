package cn.zhangchuangla.framework.model.entity.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 内存信息
 *
 * @author Chuang
 * <p>
 * created on 2025/3/19 19:47
 */
@Data
@Schema(description = "内存信息")
public class Memory {

    /**
     * 内存总量(GB)
     */
    @Schema(description = "内存总量(GB)")
    private double total;

    /**
     * 已用内存(GB)
     */
    @Schema(description = "已用内存(GB)")
    private double used;

    /**
     * 剩余内存(GB)
     */
    @Schema(description = "剩余内存(GB)")
    private double free;

    /**
     * 内存使用率
     */
    @Schema(description = "内存使用率")
    private String usage;

    /**
     * 交换内存总量(GB)
     */
    @Schema(description = "交换内存总量(GB)")
    private double swapTotal;

    /**
     * 已用交换内存(GB)
     */
    @Schema(description = "已用交换内存(GB)")
    private double swapUsed;

    /**
     * 剩余交换内存(GB)
     */
    @Schema(description = "剩余交换内存(GB)")
    private double swapFree;

    /**
     * 交换内存使用率
     */
    @Schema(description = "交换内存使用率")
    private String swapUsage;
}
