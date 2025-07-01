package cn.zhangchuangla.storage.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/1 02:53
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SimpleFileVO {

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
     * 文件路径
     */
    @Schema(description = "文件URL")
    private String fileUrl;


    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String fileType;
}
