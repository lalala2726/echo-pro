package cn.zhangchuangla.storage.model.vo.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 文件配置表
 *
 * @author Chuang
 */
@Data
@Schema(description = "文件配置列表视图")
public class StorageConfigListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

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
     * 是否主配置
     */
    @Schema(description = "是否主配置")
    private Boolean isPrimary;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
