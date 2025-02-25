package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志, 用于记录用户登录日志
 */
@TableName(value = "sys_login_log")
@Data
@Schema(name = "系统登录日志", description = "系统登录日志")
public class SysLoginLog {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;

    /**
     * ip
     */
    @Schema(description = "登录IP")
    private String ip;

    /**
     * 地址
     */
    @Schema(description = "IP归属地")
    private String address;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Integer isDeleted;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
