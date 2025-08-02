package cn.zhangchuangla.system.core.model.vo.user;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/3 03:25
 */
@Data
@Schema(name = "用户安全日志", description = "展示用户最近的安全日志信息")
public class UserSecurityLog {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @Excel(name = "主键")
    private Long id;

    /**
     * 操作状态 (0成功1失败2未知)
     */
    @Schema(description = "操作状态 (0成功1失败2未知)")
    @Excel(name = "操作状态")
    private Integer operationStatus;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP")
    @Excel(name = "操作IP")
    private String operationIp;

    /**
     * 操作地点
     */
    @Schema(description = "地点")
    @Excel(name = "操作地点")
    private String operationRegion;

    /**
     * 操作结果
     */
    @Schema(description = "操作结果")
    @Excel(name = "操作结果")
    private String responseResult;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块")
    @Excel(name = "操作模块")
    private String module;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    @Excel(name = "操作类型")
    private String operationType;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    @Excel(name = "操作时间")
    private Date createTime;
}
