package cn.zhangchuangla.system.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 系统登录日志, 用于记录用户登录日志
 *
 * @author Chuang
 */
@TableName(value = "sys_login_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysLoginLog {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账号状态
     */
    private Integer status;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 地区
     */
    private String region;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 创建者
     */
    private String createBy;

}
