package cn.zhangchuangla.system.storage.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件传输数据传输对象
 *
 * @author Chuang
 * <p>
 * created on 2025/6/28 20:58
 */
@Data
public class FileTransferDto {

    /**
     * 文件
     */
    private MultipartFile file;


}
