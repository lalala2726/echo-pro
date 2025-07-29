package cn.zhangchuangla.framework.model.entity;

import cn.zhangchuangla.common.core.enums.DeviceType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 刷新令牌会话ID
     */
    private String refreshTokenId;

    /**
     * 设备类型
     */
    private DeviceType deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 登录地点
     */
    private String location;


}
