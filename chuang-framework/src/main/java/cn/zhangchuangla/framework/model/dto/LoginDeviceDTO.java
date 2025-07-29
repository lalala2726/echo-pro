package cn.zhangchuangla.framework.model.dto;

import cn.zhangchuangla.common.core.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/25 15:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDeviceDTO {

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
    private String refreshSessionId;

    /**
     * 设备类型
     */
    private String deviceType = DeviceType.UNKNOWN.getValue();

    /**
     * 设备名称
     */
    private String deviceName;

    private String userAgent;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 地理位置
     */
    private String location;


}
