package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.storage.FileInfo;
import cn.zhangchuangla.storage.core.StorageManager;
import cn.zhangchuangla.storage.core.StorageService;
import cn.zhangchuangla.storage.service.StorageFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    private final StorageManager storageManager;
    private final StorageFileService storageFileService;

    /**
     * 智能文件上传
     * 如果检测是图片，将进行压缩处理并上传两个版本
     *
     * @param file    上传的文件
     * @param subPath 可选的子路径
     * @return 上传结果
     */
    @PostMapping("/upload")
    @Operation(summary = "普通文件上传")
    @OperationLog(title = "文件上传", businessType = BusinessType.UPLOAD)
    public AjaxResult<HashMap<String, String>> upload(@Parameter(description = "上传的文件")
                                                      @RequestParam("file") MultipartFile file,
                                                      @Parameter(description = "可选的子路径，例如 'avatars' 或 'documents/2024'")
                                                      @RequestParam(value = "subPath", required = false) String subPath) {
        if (file.isEmpty()) {
            return error("请选择一个文件上传");
        }

        try {
            StorageService activeStorageService = storageManager.getActiveStorageService();
            if (activeStorageService == null) {
                return error("未配置存储服务，请联系管理员");
            }

            // Use a default subPath if not provided, or keep it null if services handle it
            String effectiveSubPath = (subPath == null || subPath.trim().isEmpty()) ? "common" : subPath;

            FileInfo fileInfo = activeStorageService.uploadFile(file, effectiveSubPath);
            storageFileService.saveFileInfo(fileInfo);

            HashMap<String, String> ajax = new HashMap<>();
            ajax.put(StorageConstants.FILE_URL, fileInfo.getUrl());
            ajax.put(StorageConstants.FILE_NAME, fileInfo.getNewFileName());
            ajax.put(StorageConstants.ORIGINAL_FILE_NAME, fileInfo.getOriginalFileName());
            ajax.put(StorageConstants.RELATIVE_PATH, fileInfo.getRelativePath());

            return success(ajax);
        } catch (Exception e) { // Catching generic Exception as StorageException and IOException could occur
            log.error("文件上传异常", e);
            return error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 图片上传
     * 进行压缩处理并上传两个版本 (具体压缩逻辑在StorageService实现中)
     *
     * @param file    上传的图片文件
     * @param subPath 可选的子路径
     * @return 上传结果
     */
    @OperationLog(title = "图片上传", businessType = BusinessType.UPLOAD)
    @Operation(summary = "图片上传")
    @PostMapping("/image/upload")
    public AjaxResult<HashMap<String, String>> imageUpload(@Parameter(description = "上传的图片文件")
                                                           @RequestParam("file") MultipartFile file,
                                                           @Parameter(description = "可选的子路径，例如 'images/products'")
                                                           @RequestParam(value = "subPath", required = false) String subPath) {
        if (file.isEmpty()) {
            return error("请选择一个图片上传");
        }

        try {
            StorageService activeStorageService = storageManager.getActiveStorageService();
            if (activeStorageService == null) {
                return error("未配置存储服务，请联系管理员");
            }
            String effectiveSubPath = (subPath == null || subPath.trim().isEmpty()) ? "images" : subPath;

            // The uploadImage method in StorageService is responsible for handling image-specific logic (e.g., thumbnails)
            FileInfo fileInfo = activeStorageService.uploadImage(file, effectiveSubPath);
            // Save info for the main image
            storageFileService.saveFileInfo(fileInfo);

            HashMap<String, String> ajax = getStringStringHashMap(fileInfo);
            return success(ajax);
        } catch (Exception e) {
            log.error("图片上传异常", e);
            return error("图片上传失败: " + e.getMessage());
        }
    }

    @NotNull
    private HashMap<String, String> getStringStringHashMap(FileInfo fileInfo) {
        HashMap<String, String> ajax = new HashMap<>();
        // URL of the main image
        ajax.put(Constants.ORIGINAL, fileInfo.getUrl());
        ajax.put(StorageConstants.FILE_NAME, fileInfo.getNewFileName());
        ajax.put(StorageConstants.ORIGINAL_FILE_NAME, fileInfo.getOriginalFileName());
        ajax.put(StorageConstants.RELATIVE_PATH, fileInfo.getRelativePath());
        if (fileInfo.getThumbnailUrl() != null) {
            // URL of the thumbnail, if generated
            ajax.put(Constants.PREVIEW, fileInfo.getThumbnailUrl());
            ajax.put(StorageConstants.THUMBNAIL_RELATIVE_PATH, fileInfo.getThumbnailPath());
        }
        return ajax;
    }
}
