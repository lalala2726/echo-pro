package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.entity.FileTransferDto;
import cn.zhangchuangla.storage.factory.StorageFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author zhangchuang
 * Created on 2025/4/3 21:23
 */
@RequestMapping("/common/file")
@RestController
@Tag(name = "文件相关")
public class FileController extends BaseController {


    private final SysFileConfigLoader sysFileConfigLoader;
    private final StorageFactory storageFactory;


    @Autowired
    public FileController(SysFileConfigLoader sysFileConfigLoader, StorageFactory storageFactory) {
        this.sysFileConfigLoader = sysFileConfigLoader;
        this.storageFactory = storageFactory;
    }

    /**
     * 智能文件上传
     * 如果检测是图片，将进行压缩处理并上传两个版本
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    @Log(title = "文件上传", businessType = BusinessType.UPLOAD)
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file) {
        AjaxResult ajax = AjaxResult.success();
        String currentDefaultUploadType = sysFileConfigLoader.getCurrentDefaultUploadType();
        StorageOperation storageOperation = storageFactory.getStorageOperation(currentDefaultUploadType);
        FileTransferDto fileTransferDto = null;
        try {
            fileTransferDto = FileTransferDto.builder()
                    .fileName(file.getOriginalFilename())
                    .bytes(file.getBytes())
                    .fileType(file.getContentType())
                    .build();
        } catch (IOException e) {
            return error("文件读取失败");
        }
        FileTransferDto result = storageOperation.save(fileTransferDto);
        ajax.put(Constants.FILE_URL, result.getFileUrl());
        return ajax;
    }
}
