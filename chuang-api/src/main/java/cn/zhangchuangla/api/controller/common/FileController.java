package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.constant.StorageTypeConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.factory.StorageFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    @Operation(summary = "普通文件上传")
    @OperationLog(title = "文件上传", businessType = BusinessType.UPLOAD)
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("请选择一个文件上传");
        }

        try {
            AjaxResult ajax = AjaxResult.success();
            String currentDefaultUploadType = sysFileConfigLoader.getCurrentDefaultUploadType();
            log.info("当前使用的存储类型: {}", currentDefaultUploadType);

            StorageOperation storageOperation = storageFactory.getStorageOperation(currentDefaultUploadType);
            if (storageOperation == null) {
                return error("未配置存储服务，请联系管理员");
            }

            FileTransferDto fileTransferDto = FileTransferDto.builder()
                    .fileName(file.getOriginalFilename())
                    .bytes(file.getBytes())
                    .fileType(file.getContentType())
                    .build();

            FileTransferDto result = storageOperation.save(fileTransferDto);
            ajax.put(StorageTypeConstants.FILE_URL, result.getFileUrl());
            return ajax;
        } catch (IOException e) {
            log.error("文件读取失败", e);
            return error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return error("文件上传失败: " + e.getMessage());
        }
    }
}
