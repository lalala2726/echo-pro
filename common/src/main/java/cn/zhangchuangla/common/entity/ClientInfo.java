package cn.zhangchuangla.common.entity;

import lombok.Data;

/**
 * 用于记录客户端信息
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 20:33
 */
@Data
public class ClientInfo {

    /**
     * ip
     */
    private String ip;

    /**
     * 地址
     */
    private String address;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

}
