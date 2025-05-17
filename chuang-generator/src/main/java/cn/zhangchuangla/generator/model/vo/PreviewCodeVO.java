package cn.zhangchuangla.generator.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码预览
 *
 * @author Chuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代码预览")
public class PreviewCodeVO {

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 代码内容
     */
    @Schema(description = "代码内容")
    private String content;
}