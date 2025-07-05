package cn.zhangchuangla.storage.model.request.config;

import cn.zhangchuangla.common.core.core.entity.base.BasePageRequest;
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
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String storageName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名")
    private String storageKey;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 存储值
     */
    @Schema(description = "存储值")
    private String storageValue;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
