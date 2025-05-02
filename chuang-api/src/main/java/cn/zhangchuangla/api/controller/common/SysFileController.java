package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.factory.StorageFactory;
import cn.zhangchuangla.storage.loader.StorageConfigLoader;
import cn.zhangchuangla.storage.service.StorageManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

/**
 * 文件相关接口
 * 提供文件上传等功能
 *
 * @author Chuang
 * Created on 2025/4/3 21:23
 */
@RequestMapping("/common/file")
@RestController
@Tag(name = "文件相关")
@Slf4j
@RequiredArgsConstructor
public class SysFileController extends BaseController {

    private final StorageConfigLoader sysFileConfigLoader;
    private final StorageFactory storageFactory;
    private final StorageManagementService storageManagementService;

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
    public AjaxResult upload(@Parameter(description = "上传的文件")
                             @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("请选择一个文件上传");
        }

        try {
            HashMap<String, String> ajax = new HashMap<>();
            String currentDefaultUploadType = sysFileConfigLoader.getCurrentDefaultUploadType();
            log.info("当前使用的存储类型: {}", currentDefaultUploadType);

            StorageOperation storageOperation = storageFactory.getStorageOperation(currentDefaultUploadType);
            if (storageOperation == null) {
                return error("未配置存储服务，请联系管理员");
            }

            FileTransferDto fileTransferDto = FileTransferDto.builder()
                    .originalName(file.getOriginalFilename())
                    .bytes(file.getBytes())
                    .contentType(file.getContentType())
                    .build();

            FileTransferDto result = storageOperation.fileUpload(fileTransferDto);
            storageManagementService.saveFileInfo(result);
            ajax.put(StorageConstants.FILE_URL, result.getOriginalFileUrl());
            return success(ajax);
        } catch (IOException e) {
            log.error("文件读取失败", e);
            return error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 图片上传
     * 进行压缩处理并上传两个版本
     *
     * @param file 上传的图片文件
     * @return 上传结果
     */
    @OperationLog(title = "图片上传", businessType = BusinessType.UPLOAD)
    @Operation(summary = "图片上传")
    @PostMapping("/image/upload")
    public AjaxResult imageUpload(@Parameter(description = "上传的图片文件")
                                  @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("请选择一个图片上传");
        }
        HashMap<String, String> ajax = new HashMap<>();
        String currentDefaultUploadType = sysFileConfigLoader.getCurrentDefaultUploadType();
        StorageOperation storageOperation = storageFactory.getStorageOperation(currentDefaultUploadType);
        try {
            FileTransferDto fileTransferDto = FileTransferDto.builder()
                    .originalName(file.getOriginalFilename())
                    .bytes(file.getBytes())
                    .contentType(file.getContentType())
                    .build();
            FileTransferDto result = storageOperation.imageUpload(fileTransferDto);
            // 保存文件信息到数据库
            storageManagementService.saveFileInfo(result);
            ajax.put(Constants.ORIGINAL, result.getOriginalFileUrl());
            ajax.put(Constants.PREVIEW, result.getPreviewImageUrl());
        } catch (IOException e) {
            return error("文件读取失败: " + e.getMessage());
        }
        return success(ajax);
    }
}
