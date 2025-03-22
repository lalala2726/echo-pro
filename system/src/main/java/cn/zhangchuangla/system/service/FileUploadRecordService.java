package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author zhangchuang
 */
public interface FileUploadRecordService extends IService<FileUploadRecord> {





    /**
     * 保存文件信息到数据库中
     */
    boolean saveFileInfo(String fileUrl, MultipartFile file, String storageType);

}
