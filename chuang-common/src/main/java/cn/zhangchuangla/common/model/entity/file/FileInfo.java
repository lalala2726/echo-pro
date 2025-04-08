package cn.zhangchuangla.common.model.entity.file;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileOperationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用于存储从MultipartFile中提取的信息
 * 避免多次读取MultipartFile导致的失效问题
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


    /**
     * 从MultipartFile创建FileInfo对象
     *
     * @param file MultipartFile对象
     * @return FileInfo对象
     */
    public static FileInfo fromMultipartFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileException(ResponseCode.FileNameIsNull);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        //获取文件Md5值
        String md5 = FileOperationUtils.calculateMD5(file.getBytes());
        return new FileInfo(
                originalFilename,
                file.getContentType(),
                file.getSize(),
                file.getBytes(), // 立即读取文件内容到内存
                fileExtension,
                md5

        );
    }
}
