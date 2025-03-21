package cn.zhangchuangla.common.enums;

import cn.zhangchuangla.common.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 20:27
 */
@Getter
@Schema(description = "默认文件上传枚举")
public enum DefaultFileUploadEnum {

    /**
     * 本地
     */
    @Schema(description = "本地上传")
    LOCAL(Constants.LOCAL_FILE_UPLOAD),

    /**
     * minio
     */
    @Schema(description = "minio")
    MINIO(Constants.MINIO_FILE_UPLOAD),

    /**
     * 阿里云
     */
    @Schema(description = "阿里云OSS")
    ALIYUN(Constants.ALIYUN_OSS_FILE_UPLOAD);

    private final String name;

    DefaultFileUploadEnum(String name) {
        this.name = name;
    }

    public static DefaultFileUploadEnum getByName(String name) {
        for (DefaultFileUploadEnum value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
