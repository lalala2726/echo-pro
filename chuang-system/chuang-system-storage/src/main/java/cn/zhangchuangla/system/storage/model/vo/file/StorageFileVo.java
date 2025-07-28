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
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 当前文件名
     */
    @Schema(description = "当前文件名")
    private String fileName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    @Schema(description = "文件类型，如 image/jpeg, application/pdf 等")
    private String contentType;

    /**
     * 文件大小，单位字节
     */
    @Schema(description = "文件大小，单位字节")
    private Long fileSize;

    /**
     * 原始文件URL，直接访问地址
     */
    @Schema(description = "原始文件URL，直接访问地址")
    private String originalFileUrl;

    /**
     * 原始文件相对路径，存储在服务器上的路径
     */
    @Schema(description = "原始文件相对路径，存储在服务器上的路径")
    private String originalRelativePath;

    /**
     * 压缩文件URL，用于图片预览等场景
     */
    @Schema(description = "压缩文件URL，用于图片预览等场景")
    private String previewImageUrl;

    /**
     * 压缩文件相对路径，存储在服务器上的路径
     */
    @Schema(description = "压缩文件相对路径，存储在服务器上的路径")
    private String previewRelativePath;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型 (LOCAL/MINIO/ALIYUN_OSS)")
    private String storageType;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID")
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称")
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private Date uploadTime;

    /**
     * 源文件回收站路径
     */
    @Schema(description = "源文件回收站路径")
    private String originalTrashPath;

    /**
     * 预览图文件回收站路径
     */
    @Schema(description = "预览图文件回收站路径")
    private String previewTrashPath;

    /**
     * 是否回收站 (0-否, 1-是)
     */
    @Schema(description = "是否回收站 (0-否, 1-是)")
    private Integer isTrash = 0;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}
