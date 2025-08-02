package cn.zhangchuangla.system.storage.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/28 21:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageVo {


    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private String fileSize;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 原始图片URL
     */
    @Schema(description = "原始图片URL")
    private String fileUrl;

    /**
     * 预览图片URL
     */
    @Schema(description = "预览图片URL")
    private String previewUrl;
}
