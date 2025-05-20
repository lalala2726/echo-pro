package cn.zhangchuangla.system.model.request.log;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统操作日志列表请求类
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统操作日志列表请求类", description = "系统操作日志列表请求类")
public class SysOperationLogQueryRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", example = "1", type = "integer", format = "int64")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1001", type = "integer", format = "int64")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin", type = "string")
    private String userName;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式", example = "GET", type = "string")
    private String requestMethod;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP", example = "127.0.0.1", type = "string")
    private String operationIp;

    /**
     * 操作结果
     */
    @Schema(description = "操作结果", example = "success", type = "string")
    private String operationResult;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块", example = "用户管理", type = "string")
    private String module;


    /**
     * 操作时间
     */
    @Schema(description = "操作时间", example = "2025-04-05T12:00:00Z", type = "date")
    private Date createTime;
}
