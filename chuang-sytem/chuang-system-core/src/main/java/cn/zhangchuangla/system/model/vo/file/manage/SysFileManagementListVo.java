package cn.zhangchuangla.system.model.vo.file.manage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 文件管理列表视图对象
 */
@Data
public class SysFileManagementListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String originalName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    @Schema(description = "文件类型，如 image/jpeg, application/pdf 等")
    private String contentType;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private String fileSize;


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
     * 存储桶名称（OSS/MINIO 使用）
     */
    @Schema(description = "存储桶名称（OSS/MINIO 使用）")
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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}
