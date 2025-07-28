package cn.zhangchuangla.system.core.model.vo.log;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Chuang
 */
@Data
@Schema(name = "系统操作日志视图对象", description = "系统操作日志视图对象")
public class SysOperationLogVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @Excel(name = "主键")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @Excel(name = "用户名")
    private String userName;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式")
    @Excel(name = "请求方式")
    private String requestMethod;

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
     * 请求地址
     */
    @Schema(description = "请求地址")
    @Excel(name = "请求地址")
    private String requestUrl;

    /**
     * 方法名称
     */
    @Schema(description = "方法名称")
    @Excel(name = "方法名称")
    private String methodName;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    @Excel(name = "请求参数")
    private String requestParams;


    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    @Excel(name = "错误信息")
    private String errorMsg;

    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）")
    @Excel(name = "耗时（毫秒）")
    private Long costTime;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    @Excel(name = "操作时间")
    private Date createTime;
}
