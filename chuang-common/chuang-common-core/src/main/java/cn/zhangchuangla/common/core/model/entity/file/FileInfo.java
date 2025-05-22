package cn.zhangchuangla.common.core.model.entity.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于存储从MultipartFile中提取的信息
 * 避免多次读取MultipartFile导致的失效问题
 *
 * @author Chuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    /**
     * 文件原始名称
     */
    private String originalFilename;

    /**
     * 文件内容类型
     */
    private String contentType;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件内容
     */
    private byte[] data;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件MD5值
     */
    private String md5;
}
