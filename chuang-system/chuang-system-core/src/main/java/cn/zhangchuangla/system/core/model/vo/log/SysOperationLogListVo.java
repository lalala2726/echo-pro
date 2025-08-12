package cn.zhangchuangla.system.core.model.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统操作日志列表视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统操作日志列表视图对象", description = "系统操作日志列表视图对象")
public class SysOperationLogListVo {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "number", example = "1")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", type = "number", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", example = "admin")
    private String userName;

    /**
     * 操作状态 (0成功1失败2未知)
     */
    @Schema(description = "操作状态 (0成功1失败2未知)", type = "number", example = "0")
    private Integer operationStatus;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式", type = "string", example = "POST")
    private String requestMethod;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP", type = "string", example = "127.0.0.1")
    private String operationIp;

    /**
     * 操作地区
     */
    @Schema(description = "操作地区", type = "string", example = "中国")
    private String operationRegion;

    /**
     * 操作结果
     */
    @Schema(description = "操作结果", type = "string", example = "成功")
    private String operationResult;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块", type = "string", example = "系统管理")
    private String module;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型", type = "string", example = "登录")
    private String operationType;

    /**
     * 请求地址
     */
    @Schema(description = "请求地址", type = "string", example = "/login")
    private String requestUrl;

    /**
     * 方法名称
     */
    @Schema(description = "方法名称", type = "string", example = "login")
    private String methodName;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数", type = "string", example = "username=admin&password=123456")
    private String params;

    /**
     * 响应状态码
     */
    @Schema(description = "响应状态码", type = "number", example = "200")
    private Integer resultCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息", type = "string", example = "用户名或密码错误")
    private String errorMsg;

    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）", type = "number", example = "100")
    private Long costTime;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间", type = "string")
    private Date createTime;
}
