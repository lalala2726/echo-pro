package cn.zhangchuangla.common.model.request;

import cn.zhangchuangla.common.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 阿里云OSS配置实体类
 *
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:40
 */
@Data
@Schema(description = "阿里云OSS配置实体类")
public class AliyunOSSConfigRequest {


    /**
     * 文件配置标志
     */
    @Schema(description = "文件配置标志", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件配置标志不能为空")
    private String storageName;


    /**
     * 参数键名
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键名不能为空")
    private String storageKey;


    /**
     * 访问端点
     */
    @Schema(description = "访问端点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "访问端点不能为空")
    private String endpoint;


    /**
     * 阿里云账号AccessKey
     */
    @Schema(description = "阿里云账号AccessKey", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "阿里云账号Access不能为空")
    private String accessKeyId;

    /**
     * 阿里云账号AccessKey Secret
     */
    @Schema(description = "阿里云账号AccessKey Secret", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "阿里云账号AccessKey Secret不能为空")
    private String accessKeySecret;

    /**
     * 存储空间名称
     */
    @Schema(description = "存储空间名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存储空间名称不能为空")
    private String bucketName;

    /**
     * 域名
     */
    @Pattern(regexp = RegularConstants.Storage.domain,
            message = "访问端点域名格式不正确")
    @Schema(description = "文件访问路径,如果为空将直接返回相对路径", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileDomain;

}
