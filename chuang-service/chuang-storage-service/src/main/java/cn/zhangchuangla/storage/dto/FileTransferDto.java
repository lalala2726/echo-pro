package cn.zhangchuangla.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件传输对象
 * 用于文件上传、下载等操作的数据传输
 *
 * @author Chuang
 *         <p>
 *         created on 2025/4/2 20:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileTransferDto {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件字节数组
     */
    private byte[] bytes;

    /**
     * 文件类型，如image/jpeg, application/pdf等
     */
    private String fileType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小，格式化后的字符串，如"1.5MB"
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
}
