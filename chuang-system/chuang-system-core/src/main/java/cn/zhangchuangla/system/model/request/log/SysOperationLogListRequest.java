package cn.zhangchuangla.system.model.request.log;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统操作日志列表请求类
 * @author zhangchuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "系统操作日志列表请求类", description = "系统操作日志列表请求类")
public class SysOperationLogListRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String requestMethod;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String operationIp;

    /**
     * 操作结果
     */
    @Schema(description = "操作结果", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String operationResult;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String module;


    /**
     * 操作时间
     */
    @Schema(description = "操作时间", type = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date createTime;
}
