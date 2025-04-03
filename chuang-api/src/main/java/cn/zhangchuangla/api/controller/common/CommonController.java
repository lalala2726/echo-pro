package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.entity.FileTransferDto;
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

@Slf4j
@Tag(name = "通用接口")
@RequestMapping("/common")
@RestController
public class CommonController extends BaseController {


    private final SysFileConfigLoader sysFileConfigLoader;
    private final StorageFactory storageFactory;

    @Autowired
    public CommonController(SysFileConfigLoader sysFileConfigLoader, StorageFactory storageFactory) {
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
    @Operation(summary = "智能文件上传", description = "上传文件，如果上传的是图片，将进行压缩处理并上传两个版本")
    @PostMapping("/upload")
    @Log(title = "文件上传", businessType = BusinessType.INSERT)
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        String currentDefaultUploadType = sysFileConfigLoader.getCurrentDefaultUploadType();
        StorageOperation storageOperation = storageFactory.getStorageOperation(currentDefaultUploadType);
        FileTransferDto fileTransferDto = FileTransferDto.builder()
                .fileName(file.getOriginalFilename())
                .bytes(file.getBytes())
                .fileType(file.getContentType())
                .build();
        storageOperation.save(fileTransferDto);
        return success();
    }


}
