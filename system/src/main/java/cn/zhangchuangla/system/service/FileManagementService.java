package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.entity.file.FileInfo;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.model.request.file.FileManagementListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * author zhangchuang
 */
public interface FileManagementService extends IService<cn.zhangchuangla.system.model.entity.FileManagement> {


    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    Page<FileManagement> fileList(@Param("request") FileManagementListRequest request);

    /**
     * 删除文件,支持批量删除
     *
     * @param ids 文件ID集合
     */
    void deleteFile(List<Long> ids);

    void saveFileRecord(String fileUrl, String compressedUrl, FileInfo fileInfo, String storageType);
}
