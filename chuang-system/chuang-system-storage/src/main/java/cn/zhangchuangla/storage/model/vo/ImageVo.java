package cn.zhangchuangla.storage.model.vo;

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
     * 原始图片URL
     */
    @Schema(description = "原始图片URL")
    private String originalUrl;

    /**
     * 预览图片URL
     */
    @Schema(description = "预览图片URL")
    private String previewUrl;
}
