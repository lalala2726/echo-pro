package cn.zhangchuangla.common.model.request;

import cn.zhangchuangla.common.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * minio 配置实体类
 *
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:41
 */
@Data
@Schema(name = "minio 配置请求对象", description = "minio 配置请求对象")
public class MinioConfigRequest {


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
    @Pattern(regexp = RegularConstants.Storage.DOMAIN, message = "访问端点域名格式不正确")
    private String endpoint;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "访问密钥不能为空")
    private String accessKey;

    /**
     * 密钥
     */
    @Schema(description = "密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密钥不能为空")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = RegularConstants.Storage.BUCKET_NAME,
            message = "存储桶名称只能包含小写字母、数字、点号和短横线，且必须以小写字母或数字开头和结尾，长度在 3 到 63 个字符之间")
    @NotBlank(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问路径,如果为空将直接返回相对路径")
    @Pattern(regexp = RegularConstants.Storage.DOMAIN,
            message = "文件访问域名格式不正确")
    private String fileDomain;

    /**
     * 是否启用文件回收站 0 否 1 是
     */
    @Schema(description = "是否启用文件回收站，当选择此功能后文件就算是永久删除了也不会真正删除，而是放入回收站，默认不启用,1启用，0不启用")
    private Integer enableTrash = 0;


}
