package cn.zhangchuangla.system.model.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统操作日志列表视图对象
 */
@Data
@Schema(description = "系统操作日志列表视图对象")
public class SysOperationLogListVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String userName;

    /**
     * 操作状态 (0成功1失败2未知)
     */
    @Schema(description = "操作状态 (0成功1失败2未知)")
    private Integer operationStatus;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式")
    private String requestMethod;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP")
    private String operationIp;

    /**
     * 操作结果
     */
    @Schema(description = "操作结果")
    private String operationResult;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块")
    private String module;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private String operationType;

    /**
     * 请求地址
     */
    @Schema(description = "请求地址")
    private String requestUrl;

    /**
     * 方法名称
     */
    @Schema(description = "方法名称")
    private String methodName;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private String params;

    /**
     * 响应状态码
     */
    @Schema(description = "响应状态码")
    private Integer resultCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）")
    private Long costTime;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private Date createTime;
}
