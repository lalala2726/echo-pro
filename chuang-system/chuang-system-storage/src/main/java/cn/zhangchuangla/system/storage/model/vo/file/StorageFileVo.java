package cn.zhangchuangla.system.storage.model.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 文件详情
 *
 * @author Chuang
 */
@Data
@Schema(description = "文件上传记录表")
public class StorageFileVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", example = "1")
    private Long id;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", type = "string", example = "example.jpg")
    private String originalName;

    /**
     * 当前文件名
     */
    @Schema(description = "当前文件名", type = "string", example = "example_20230401120000.jpg")
    private String fileName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    @Schema(description = "文件类型，如 image/jpeg, application/pdf 等", type = "string", example = "image/jpeg")
    private String contentType;

    /**
     * 文件大小，单位字节
     */
    @Schema(description = "文件大小，单位字节", type = "integer", example = "102400")
    private Long fileSize;

    /**
     * 原始文件URL，直接访问地址
     */
    @Schema(description = "原始文件URL，直接访问地址", type = "string", example = "https://example.com/files/example.jpg")
    private String originalFileUrl;

    /**
     * 原始文件相对路径，存储在服务器上的路径
     */
    @Schema(description = "原始文件相对路径，存储在服务器上的路径", type = "string", example = "/files/example.jpg")
    private String originalRelativePath;

    /**
     * 压缩文件URL，用于图片预览等场景
     */
    @Schema(description = "压缩文件URL，用于图片预览等场景", type = "string", example = "https://example.com/files/thumb_example.jpg")
    private String previewImageUrl;

    /**
     * 压缩文件相对路径，存储在服务器上的路径
     */
    @Schema(description = "压缩文件相对路径，存储在服务器上的路径", type = "string", example = "/files/thumb_example.jpg")
    private String previewRelativePath;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", type = "string", example = "jpg")
    private String fileExtension;

    /**
     * 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型 (LOCAL/MINIO/ALIYUN_OSS)", type = "string", example = "LOCAL")
    private String storageType;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", type = "string", example = "my-bucket")
    private String bucketName;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID", type = "integer", example = "1001")
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称", type = "string", example = "张三")
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间", type = "string", format = "date-time", example = "2023-04-01T12:00:00Z")
    private Date uploadTime;

    /**
     * 源文件回收站路径
     */
    @Schema(description = "源文件回收站路径", type = "string", example = "/trash/files/example.jpg")
    private String originalTrashPath;

    /**
     * 预览图文件回收站路径
     */
    @Schema(description = "预览图文件回收站路径", type = "string", example = "/trash/files/thumb_example.jpg")
    private String previewTrashPath;

    /**
     * 是否回收站 (0-否, 1-是)
     */
    @Schema(description = "是否回收站 (0-否, 1-是)", type = "integer", example = "0")
    private Integer isTrash = 0;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", type = "string", format = "date-time", example = "2023-04-01T12:00:00Z")
    private Date updateTime;
}
