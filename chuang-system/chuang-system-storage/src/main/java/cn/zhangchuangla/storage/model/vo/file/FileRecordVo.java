package cn.zhangchuangla.storage.model.vo.file;

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
public class FileRecordVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "当前文件名")
    private String fileName;

    @Schema(description = "文件类型，如 image/jpeg, application/pdf 等")
    private String contentType;

    @Schema(description = "文件大小，单位字节")
    private Long fileSize;

    @Schema(description = "原始文件URL，直接访问地址")
    private String originalFileUrl;

    @Schema(description = "原始文件相对路径，存储在服务器上的路径")
    private String originalRelativePath;

    @Schema(description = "压缩文件URL，用于图片预览等场景")
    private String previewImageUrl;

    @Schema(description = "压缩文件相对路径，存储在服务器上的路径")
    private String previewRelativePath;

    @Schema(description = "文件扩展名")
    private String fileExtension;

    @Schema(description = "存储类型 (LOCAL/MINIO/ALIYUN_OSS)")
    private String storageType;

    @Schema(description = "存储桶名称")
    private String bucketName;

    @Schema(description = "上传者ID")
    private Long uploaderId;

    @Schema(description = "上传者名称")
    private String uploaderName;

    @Schema(description = "上传时间")
    private Date uploadTime;

    @Schema(description = "源文件回收站路径")
    private String originalTrashPath;

    @Schema(description = "预览图文件回收站路径")
    private String previewTrashPath;

    @Schema(description = "是否回收站 (0-否, 1-是)")
    private Integer isTrash = 0;

    @Schema(description = "更新时间")
    private Date updateTime;
}
