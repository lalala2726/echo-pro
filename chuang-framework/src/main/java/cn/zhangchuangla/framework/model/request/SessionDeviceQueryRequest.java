package cn.zhangchuangla.framework.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.common.core.enums.DeviceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录设备
 *
 * @author Chuang
 * <p>
 * created on 2025/7/26 20:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionDeviceQueryRequest extends BasePageRequest {

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
    private Long loginTime;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 登录地点
     */
    private String location;


}
