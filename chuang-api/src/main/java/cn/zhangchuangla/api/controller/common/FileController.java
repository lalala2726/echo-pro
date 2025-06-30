package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.service.StorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件相关接口
 * 提供文件上传等功能
 *
 * @author Chuang
 * Created on 2025/4/3 21:23
 */
@RequestMapping("/common/file")
@RestController
@Tag(name = "文件相关接口", description = "提供多种文件和资源上传相关接口")
@Slf4j
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final StorageService storageService;


    @PostMapping
    public AjaxResult<UploadedFileInfo> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UploadedFileInfo upload = storageService.uploadImage(file);
        return success(upload);
    }

}
