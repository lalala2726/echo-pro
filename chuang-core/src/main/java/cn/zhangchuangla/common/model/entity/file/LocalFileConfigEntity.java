package cn.zhangchuangla.common.model.entity.file;

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
public class LocalFileConfigEntity {

    /**
     * 文件上传路径
     */
    private String uploadPath;


}
