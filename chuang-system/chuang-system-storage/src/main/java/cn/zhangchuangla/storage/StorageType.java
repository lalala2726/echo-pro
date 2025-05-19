package cn.zhangchuangla.storage;

import lombok.Getter;

/**
 * 不同存储提供程序的枚举。
 *
 * @author Chuang
 */
@Getter
public enum StorageType {
    LOCAL("local", "本地存储"),
    MINIO("minio", "MinIO"),
    ALIYUN_OSS("aliyun_oss", "阿里云OSS"),
    TENCENT_COS("tencent_cos", "腾讯云COS");

    private final String code;
    private final String description;

    StorageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static StorageType fromCode(String code) {
        for (StorageType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown storage type code: " + code);
    }

}
