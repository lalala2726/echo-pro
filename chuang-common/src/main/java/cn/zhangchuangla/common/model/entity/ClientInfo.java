package cn.zhangchuangla.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 16:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientInfo {

    /**
     * 系统名称
     */
    private String osName;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 设备版本
     */
    private String osVersion;

    /**
     * 浏览器类型
     */
    private String browserType;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 浏览器渲染引擎
     */
    private String browserRenderingEngine;

    /**
     * 区域
     */
    private String region;

}
