package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统操作日志实体
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    /**
     * 请求的IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 请求方式（GET, POST等）
     */
    @TableField("request_method")
    private String requestMethod;

}