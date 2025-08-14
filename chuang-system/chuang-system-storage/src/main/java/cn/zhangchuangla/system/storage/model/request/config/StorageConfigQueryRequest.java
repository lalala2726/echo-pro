package cn.zhangchuangla.system.storage.model.request.config;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件配置表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文件列表请求类")
public class StorageConfigQueryRequest extends BasePageRequest {

    /**
     * 存储名称
     */
    @Schema(description = "存储名称", type = "string", example = "minio")
    private String storageName;

    /**
     * 存储键名
     */
    @Schema(description = "存储键名", type = "string", example = "minio")
    private String storageKey;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型", type = "string", example = "minio")
    private String storageType;

}
