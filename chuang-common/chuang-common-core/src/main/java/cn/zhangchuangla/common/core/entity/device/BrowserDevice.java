package cn.zhangchuangla.common.core.entity.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/24 23:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrowserDevice {

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 设备类型 PC Mobile Tablet
     */
    private String deviceType;

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 浏览器版本
     */
    private String browserVersion;


}
