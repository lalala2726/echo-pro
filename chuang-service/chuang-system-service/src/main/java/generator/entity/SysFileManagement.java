package generator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 文件上传记录表
 */
@TableName(value = "sys_file_management")
@Data
public class SysFileManagement {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String originalName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    private String contentType;

    /**
     * 文件大小，格式化后的字符串，如 "1.5MB"
     */
    private String fileSize;

    /**
     * 文件MD5值，用于文件完整性校验
     */
    private String fileMd5;

    /**
     * 原始文件URL，直接访问地址
     */
    private String originalFileUrl;

    /**
     * 原始文件相对路径，存储在服务器上的路径
     */
    private String originalRelativePath;

    /**
     * 压缩文件URL，用于图片预览等场景
     */
    private String compressedFileUrl;

    /**
     * 压缩文件相对路径，存储在服务器上的路径
     */
    private String compressedRelativePath;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     */
    private String storageType;

    /**
     * 存储桶名称（OSS/MINIO 使用）
     */
    private String bucketName;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 上传者名称
     */
    private String uploaderName;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
