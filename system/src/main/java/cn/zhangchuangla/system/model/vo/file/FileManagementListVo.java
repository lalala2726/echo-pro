package cn.zhangchuangla.system.model.vo.file;

import cn.zhangchuangla.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件上传记录表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文件列表VO")
public class FileManagementListVo extends BaseVO {

    @Schema(description = "文件ID")
    private Long id;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originalFileName;

    /**
     * 文件存储路径
     */
    @Schema(description = "文件存储路径")
    private String filePath;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    /**
     * 文件类型/MIME类型
     */
    @Schema(description = "文件类型/MIME类型")
    private String fileType;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 存储类型(LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型(LOCAL/MINIO/ALIYUN_OSS)")
    private String storageType;


    /**
     * 文件MD5值
     */
    @Schema(description = "文件MD5值")
    private String md5;

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

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}