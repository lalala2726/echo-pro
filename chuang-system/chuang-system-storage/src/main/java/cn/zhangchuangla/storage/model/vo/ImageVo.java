package cn.zhangchuangla.storage.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/28 21:03
 */
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
