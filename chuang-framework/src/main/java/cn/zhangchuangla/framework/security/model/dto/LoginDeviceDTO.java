package cn.zhangchuangla.framework.security.model.dto;

import cn.zhangchuangla.common.core.enums.DeviceType;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 刷新令牌会话ID
     */
    @NotNull(message = "刷新令牌会话ID不能为空")
    private String refreshSessionId;

    /**
     * 设备类型
     */
    private String deviceType = DeviceType.UNKNOWN.getValue();

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 地理位置
     */
    private String location;


}
