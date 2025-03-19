package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统操作日志实体类
 * 记录用户的操作行为，用于审计和排查问题
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    
    /**
     * 日志主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 操作模块
     */
    @TableField("module")
    private String module;
    
    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;
    
    /**
     * 操作用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 操作用户名
     */
    @TableField("user_name")
    private String userName;
    
    /**
     * 请求URL
     */
    @TableField("request_url")
    private String requestUrl;
    
    /**
     * 请求方法(GET/POST等)
     */
    @TableField("request_method")
    private String requestMethod;
    
    /**
     * 操作者IP地址
     */
    @TableField("operation_ip")
    private String operationIp;
    
    /**
     * IP地址位置信息
     */
    @TableField("ip_location")
    private String ipLocation;
    
    /**
     * 方法名称
     */
    @TableField("method_name")
    private String methodName;
    
    /**
     * 类名称
     */
    @TableField("class_name")
    private String className;
    
    /**
     * 请求参数
     */
    @TableField("params")
    private String params;
    
    /**
     * 返回结果
     */
    @TableField("result")
    private String result;
    
    /**
     * 状态(1成功，0失败)
     */
    @TableField("status")
    private Integer status;
    
    /**
     * HTTP结果状态码
     */
    @TableField("result_code")
    private Integer resultCode;
    
    /**
     * 错误消息
     */
    @TableField("error_msg")
    private String errorMsg;
    
    /**
     * 异常堆栈信息
     */
    @TableField("exception_stack")
    private String exceptionStack;
    
    /**
     * 执行耗时(毫秒)
     */
    @TableField("cost_time")
    private Long costTime;
    
    /**
     * 操作时间
     */
    @TableField("create_time")
    private Date createTime;
}