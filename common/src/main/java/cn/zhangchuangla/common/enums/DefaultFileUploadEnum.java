package cn.zhangchuangla.common.enums;

import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 20:27
 */
@Getter
public enum DefaultFileUploadEnum {

    /**
     * 本地
     */
    LOCAL("local"),

    /**
     * minio
     */
    MINIO("minio"),

    /**
     * 阿里云
     */
    ALIYUN("oss");

    private final String name;

    DefaultFileUploadEnum(String name) {
        this.name = name;
    }

}
