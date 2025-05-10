package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志表
 */
@TableName(value = "sys_operation_log")
@Data
public class SysOperationLog {
    //todo 增加操作地点
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作地点
     */
    private String operationRegion;

    /**
     * 操作状态 (0成功1失败2未知)
     */
    private Integer operationStatus;

    /**
     * 操作结果
     */
    private String responseResult;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 耗时（毫秒）
     */
    private Long costTime;

    /**
     * 操作时间
     */
    private Date createTime;
}
