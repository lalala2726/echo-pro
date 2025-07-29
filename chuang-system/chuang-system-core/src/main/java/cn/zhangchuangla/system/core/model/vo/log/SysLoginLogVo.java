package cn.zhangchuangla.system.core.model.vo.log;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统登录日志视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统登录日志视图对象", description = "系统登录日志视图对象")
public class SysLoginLogVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @Excel(name = "主键")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @Excel(name = "用户名")
    private String username;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态")
    @Excel(name = "账号状态")
    private Integer status;

    /**
     * ip地址
     */
    @Schema(description = "IP地址")
    @Excel(name = "IP地址")
    private String ip;

    /**
     * 区域
     */
    @Schema(description = "区域")
    @Excel(name = "区域")
    private String region;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器")
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    @Excel(name = "操作系统")
    private String os;

    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    @Excel(name = "登录时间")
    private Date loginTime;


    /**
     * 创建人
     */
    @Schema(description = "创建者")
    @Excel(name = "创建者")
    private String createBy;

}
