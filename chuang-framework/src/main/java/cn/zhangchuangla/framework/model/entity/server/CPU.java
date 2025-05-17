package cn.zhangchuangla.framework.model.entity.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * CPU信息
 *
 * @author Chuang
 * <p>
 * created on 2025/3/19 19:46
 */
@Data
@Alias("ServerCPU") // 使用更特定的别名避免冲突
@Schema(name = "CPU信息", description = "CPU信息")
public class CPU {

    /**
     * CPU名称
     */
    @Schema(description = "CPU名称")
    private String cpuName;

    /**
     * CPU使用率
     */
    @Schema(description = "CPU使用率")
    private String cpuUsage;

    /**
     * CPU核数（逻辑处理器数量）
     */
    @Schema(description = "CPU核数（逻辑处理器数量）")
    private String cpuCore;

    /**
     * 物理CPU数量
     */
    @Schema(description = "物理CPU数量")
    private String physicalPackageCount;

    /**
     * 物理核心数
     */
    @Schema(description = "物理核心数")
    private String physicalProcessorCount;

    /**
     * CPU厂商
     */
    @Schema(description = "CPU厂商")
    private String cpuVendor;

    /**
     * CPU型号
     */
    @Schema(description = "CPU型号")
    private String cpuModel;

    /**
     * CPU系列
     */
    @Schema(description = "CPU系列")
    private String cpuFamily;

    /**
     * CPU步进
     */
    @Schema(description = "CPU步进")
    private String cpuStepping;

    /**
     * CPU标识符
     */
    @Schema(description = "CPU标识符")
    private String cpuIdentifier;

    /**
     * CPU频率
     */
    @Schema(description = "CPU频率")
    private String cpuFrequency;

    /**
     * 最大频率
     */
    @Schema(description = "最大频率")
    private String maxFreq;

    /**
     * 各核心使用率
     */
    @Schema(description = "各核心使用率")
    private List<String> coreUsages;

}
