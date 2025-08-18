package cn.zhangchuangla.system.storage.model.vo.file;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 文件管理列表视图对象
 *
 * @author Chuang
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorageFileListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", example = "1")
    @Excel(name = "文件名")
    private Long id;

    /**
     * 文件名
     */
    @Schema(description = "文件名", type = "string", example = "example.jpg")
    @Excel(name = "文件名")
    private String originalName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    @Schema(description = "文件类型，如 image/jpeg, application/pdf 等", type = "string", example = "image/jpeg")
    @Excel(name = "文件类型")
    private String contentType;

    /**
     * 压缩文件URL，用于图片预览等场景
     */
    @Schema(description = "压缩文件URL，用于图片预览等场景", type = "string", example = "http://example.com/preview/example.jpg")
    @Excel(name = "压缩文件URL")
    private String previewImageUrl;

    /**
     * 原始文件URL，直接访问地址
     */
    @Schema(description = "原始文件URL，直接访问地址", type = "string", example = "http://example.com/original/example.jpg")
    @Excel(name = "原始文件URL")
    private String originalFileUrl;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小", type = "string", example = "1024KB")
    @Excel(name = "文件大小")
    private String fileSize;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", type = "string", example = "jpg")
    @Excel(name = "文件扩展名")
    private String fileExtension;

    /**
     * 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型 (LOCAL/MINIO/ALIYUN_OSS)", type = "string", example = "MINIO")
    @Excel(name = "存储类型")
    private String storageType;

    /**
     * 存储桶名称（OSS/MINIO 使用）
     */
    @Schema(description = "存储桶名称（OSS/MINIO 使用）", type = "string", example = "my-bucket")
    @Excel(name = "存储桶名称")
    private String bucketName;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称", type = "string", example = "张三")
    @Excel(name = "上传者名称")
    private String uploaderName;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "上传时间", type = "string", example = "2023-10-01 12:00:00")
    @Excel(name = "上传时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;


}
