package cn.zhangchuangla.system.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果
 *
 * @author zhangchuang
 * Created on 2025/3/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传结果")
public class FileUploadResult {

    /**
     * 是否为图片文件
     */
    @Schema(description = "是否为图片文件")
    private boolean isImage;

    /**
     * 原始文件URL
     */
    @Schema(description = "原始文件URL")
    private String originalUrl;

    /**
     * 压缩后的文件URL（仅图片文件有值）
     */
    @Schema(description = "压缩后的文件URL（仅图片文件有值）")
    private String compressedUrl;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 原始文件大小(字节)
     */
    @Schema(description = "原始文件大小(字节)")
    private long originalSize;

    /**
     * 压缩后文件大小(字节)
     */
    @Schema(description = "压缩后文件大小(字节)")
    private long compressedSize;
}
