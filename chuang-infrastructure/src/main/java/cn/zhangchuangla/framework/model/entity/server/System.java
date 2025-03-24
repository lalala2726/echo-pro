package cn.zhangchuangla.framework.model.entity.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统信息
 *
 * @author Chuang
 * <p>
 * created on 2025/3/19 19:47
 */
@Data
@Schema(description = "系统信息")
public class System {

    /**
     * 服务器名称
     */
    @Schema(description = "服务器名称")
    private String computerName;

    /**
     * 服务器Ip
     */
    @Schema(description = "服务器IP")
    private String computerIp;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String osName;

    /**
     * 系统架构
     */
    @Schema(description = "系统架构")
    private String osArch;

    /**
     * 操作系统版本
     */
    @Schema(description = "操作系统版本")
    private String osVersion;

    /**
     * 操作系统制造商
     */
    @Schema(description = "操作系统制造商")
    private String osManufacturer;

    /**
     * 操作系统位数
     */
    @Schema(description = "操作系统位数")
    private String osBit;

    /**
     * 项目路径
     */
    @Schema(description = "项目路径")
    private String userDir;

    /**
     * 系统启动时间
     */
    @Schema(description = "系统启动时间")
    private String bootTime;

    /**
     * 系统运行时长
     */
    @Schema(description = "系统运行时长")
    private String uptime;

    /**
     * 进程数量
     */
    @Schema(description = "进程数量")
    private int processCount;

    /**
     * 线程数量
     */
    @Schema(description = "线程数量")
    private int threadCount;
}
