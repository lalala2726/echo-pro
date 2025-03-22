package cn.zhangchuangla.admin.controller.common;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.FileUploadRecordService;
import cn.zhangchuangla.system.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
public class CommonController {


    private final FileUploadService fileUploadService;
    private final ConfigCacheService configCacheService;
    private final FileUploadRecordService fileUploadRecordService;

    public CommonController(FileUploadService fileUploadService, ConfigCacheService configCacheService, FileUploadRecordService fileUploadRecordService) {
        this.fileUploadService = fileUploadService;
        this.configCacheService = configCacheService;
        this.fileUploadRecordService = fileUploadRecordService;
    }


    /**
     * 根据配置文件选择上传文件方式进行上传文件
     *
     * @return 文件路径
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public AjaxResult upload(@Parameter(name = "文件", required = true, description = "需要上传的文件参数")
                             @RequestParam("file")
                             MultipartFile file)  {

        log.info("文件上传开始");
        String defaultFileUploadType = configCacheService.getDefaultFileUploadType();
        String fileUrl = switch (defaultFileUploadType) {
            case Constants.MINIO_FILE_UPLOAD -> fileUploadService.MinioFileUpload(file);
            case Constants.ALIYUN_OSS_FILE_UPLOAD -> fileUploadService.AliyunOssFileUpload(file);
            case Constants.LOCAL_FILE_UPLOAD -> fileUploadService.localFileUpload(file);
            default -> throw new ProfileException("无法选择上传方式，请检查文件上传配置信息");
        };
        fileUploadRecordService.saveFileInfo(fileUrl, file, defaultFileUploadType);
        //todo 文件上传成功后将文件上传信息存放到数据库中方便后续进行预览删除等操作！
        AjaxResult ajax = new AjaxResult();
        ajax.put("url", fileUrl);
        return ajax;
    }
}
