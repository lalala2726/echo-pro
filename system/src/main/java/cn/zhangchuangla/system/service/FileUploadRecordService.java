package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import cn.zhangchuangla.system.model.request.file.FileUploadRecordRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 */
public interface FileUploadRecordService extends IService<FileUploadRecord> {


    /**
     * 保存文件信息到数据库中
     */
    void saveFileInfo(String fileUrl, String compressedUrl, MultipartFile file, String storageType);


    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    Page<FileUploadRecord> fileList(FileUploadRecordRequest request);
}
