package cn.zhangchuangla.infrastructure.model.entity.server;

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
public class CPU {

    /**
     * CPU名称
     */
    private String cpuName;

    /**
     * CPU使用率
     */
    private String cpuUsage;

    /**
     * CPU核数（逻辑处理器数量）
     */
    private String cpuCore;

    /**
     * 物理CPU数量
     */
    private String physicalPackageCount;

    /**
     * 物理核心数
     */
    private String physicalProcessorCount;

    /**
     * CPU厂商
     */
    private String cpuVendor;

    /**
     * CPU型号
     */
    private String cpuModel;

    /**
     * CPU系列
     */
    private String cpuFamily;

    /**
     * CPU步进
     */
    private String cpuStepping;

    /**
     * CPU标识符
     */
    private String cpuIdentifier;

    /**
     * CPU频率
     */
    private String cpuFrequency;

    /**
     * 最大频率
     */
    private String maxFreq;

    /**
     * 各核心使用率
     */
    private List<String> coreUsages;

}
