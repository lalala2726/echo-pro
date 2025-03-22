package cn.zhangchuangla.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 文件上传记录表
 */
@TableName(value = "file_management")
@Data
public class FileManagement {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 文件相对路径
     */
    private String relativeFileLocation;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 预览图片
     */
    private String previewImage;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型/MIME类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 存储类型(LOCAL/MINIO/ALIYUN_OSS)
     */
    private String storageType;

    /**
     * 存储桶名称(OSS/MINIO使用)
     */
    private String bucketName;

    /**
     * 文件MD5值
     */
    private String md5;

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
     * 是否删除(0-未删除,1-已删除)
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

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;
}