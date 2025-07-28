package cn.zhangchuangla.framework.model.request;

import cn.zhangchuangla.common.core.enums.DeviceType;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录设备信息,所有设备信息都封装在这里
 *
 * @author Chuang
 * <p>
 * created on 2025/7/27 20:51
 */
@Data
public class DeviceInfoRequest {

    /**
     * 设备类型
     */
    @Schema(description = "设备类型", type = "string", allowableValues = {"web", "pc", "mobile", "miniProgram", "unknown"}, defaultValue = "WEB")
    private DeviceType deviceType = DeviceType.WEB;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称", type = "string", defaultValue = "Web")
    private String deviceName = "Web";

    /**
     * 设置设备类型，支持字符串自动转换为枚举
     * 如果传入的字符串不是枚举中的值，则使用默认的 UNKNOWN
     *
     * @param deviceTypeStr 设备类型字符串
     */
    @JsonSetter("deviceType")
    public void setDeviceType(String deviceTypeStr) {
        this.deviceType = DeviceType.getByValue(deviceTypeStr);
    }

    /**
     * 设置设备类型枚举
     *
     * @param deviceType 设备类型枚举
     */
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

}
