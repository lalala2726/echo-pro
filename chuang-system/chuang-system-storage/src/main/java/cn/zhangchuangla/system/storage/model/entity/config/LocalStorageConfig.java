package cn.zhangchuangla.system.storage.model.entity.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "本地文件配置实体类")
public class LocalStorageConfig {

    /**
     * 文件上传路径
     */
    private String uploadPath;

    /**
     * 文件访问路径,如果为空将直接返回相对路径
     */
    private String fileDomain;

    /**
     * 是否真实删除文件
     */
    private boolean realDelete;


}
