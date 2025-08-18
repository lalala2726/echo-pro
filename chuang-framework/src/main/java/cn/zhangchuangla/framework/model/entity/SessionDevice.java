package cn.zhangchuangla.framework.model.entity;

import cn.zhangchuangla.common.core.enums.DeviceType;
import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 登录设备
 *
 * @author Chuang
 * <p>
 * created on 2025/7/26 20:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDevice {

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    @Schema(description = "用户ID", example = "1001", type = "integer", format = "int64")
    private Long userId;

    /**
     * 用户名
     */
    @Excel(name = "用户名")
    @Schema(description = "用户名", example = "admin", type = "string")
    private String username;

    /**
     * 刷新令牌会话ID
     */
    @Excel(name = "刷新令牌会话ID")
    @Schema(description = "刷新令牌会话ID", example = "abc123xyz", type = "string")
    private String refreshTokenId;

    /**
     * 设备类型
     */
    @Excel(name = "设备类型")
    @Schema(description = "设备类型", example = "web", type = "string")
    private DeviceType deviceType;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    @Schema(description = "设备名称", example = "Chrome", type = "string")
    private String deviceName;

    /**
     * 登录时间
     */
    @Excel(name = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "登录时间", example = "2025-07-26 20:05:00", type = "string", format = "date-time")
    private Date loginTime;

    /**
     * 登录IP
     */
    @Excel(name = "登录IP")
    @Schema(description = "登录IP", example = "192.168.1.1", type = "string")
    private String ip;

    /**
     * 登录地点
     */
    @Excel(name = "登录地点")
    @Schema(description = "登录地点", example = "中国-北京", type = "string")
    private String location;


}
