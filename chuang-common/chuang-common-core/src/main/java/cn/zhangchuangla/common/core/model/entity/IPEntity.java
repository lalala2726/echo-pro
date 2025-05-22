package cn.zhangchuangla.common.core.model.entity;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 21:50
 */
@Data
public class IPEntity {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 国家
     */
    private String country;

    /**
     * 区域
     */
    private String area;

    /**
     * 运营商
     */
    private String ISP;


    /**
     * 位置详情
     */
    private String region;
}
