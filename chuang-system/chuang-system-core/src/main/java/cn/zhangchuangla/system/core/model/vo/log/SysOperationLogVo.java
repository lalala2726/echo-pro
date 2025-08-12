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
    @Schema(description = "主键", type = "integer", example = "1")
    @Excel(name = "主键")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", type = "integer", example = "1001")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", type = "string", example = "admin")
    @Excel(name = "用户名")
    private String userName;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式", type = "string", example = "POST")
    @Excel(name = "请求方式")
    private String requestMethod;

    /**
     * 操作状态 (0成功1失败2未知)
     */
    @Schema(description = "操作状态 (0成功1失败2未知)", type = "integer", example = "0")
    @Excel(name = "操作状态")
    private Integer operationStatus;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP", type = "string", example = "192.168.1.1")
    @Excel(name = "操作IP")
    private String operationIp;

    /**
     * 操作地点
     */
    @Schema(description = "地点", type = "string", example = "北京")
    @Excel(name = "操作地点")
    private String operationRegion;

    /**
     * 操作结果
     */
    @Schema(description = "操作结果", type = "string", example = "操作成功")
    @Excel(name = "操作结果")
    private String responseResult;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块", type = "string", example = "用户管理")
    @Excel(name = "操作模块")
    private String module;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型", type = "string", example = "新增")
    @Excel(name = "操作类型")
    private String operationType;

    /**
     * 请求地址
     */
    @Schema(description = "请求地址", type = "string", example = "/user/add")
    @Excel(name = "请求地址")
    private String requestUrl;

    /**
     * 方法名称
     */
    @Schema(description = "方法名称", type = "string", example = "addUser")
    @Excel(name = "方法名称")
    private String methodName;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数", type = "string", example = "{\"username\":\"admin\",\"password\":\"123456\"}")
    @Excel(name = "请求参数")
    private String requestParams;


    /**
     * 错误信息
     */
    @Schema(description = "错误信息", type = "string", example = "用户名或密码错误")
    @Excel(name = "错误信息")
    private String errorMsg;

    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）", type = "integer", example = "150")
    @Excel(name = "耗时（毫秒）")
    private Long costTime;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间", type = "string", format = "date-time", example = "2023-04-01T12:00:00Z")
    @Excel(name = "操作时间")
    private Date createTime;
}
