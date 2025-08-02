package cn.zhangchuangla.system.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 安全日志表
 *
 * @author Chuang
 */
@TableName(value = "sys_security_log")
@Data
public class SysSecurityLog {
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 日志标题
     */
    private String title;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作区域
     */
    private String operationRegion;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作时间
     */
    private Date operationTime;
}
