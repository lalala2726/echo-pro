package cn.zhangchuangla.admin.controller.common;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "通用接口")
@RequestMapping("/common")
@RestController
public class CommonController {


    private final FileUploadService fileUploadService;
    private final ConfigCacheService configCacheService;

    public CommonController(FileUploadService fileUploadService, ConfigCacheService configCacheService) {
        this.fileUploadService = fileUploadService;
        this.configCacheService = configCacheService;
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
                             MultipartFile file) {
        String defaultFileUploadType = configCacheService.getDefaultFileUploadType();
        String fileUrl = switch (defaultFileUploadType) {
            case Constants.MINIO_FILE_UPLOAD -> fileUploadService.MinioFileUpload(file);
            case Constants.ALIYUN_OSS_FILE_UPLOAD -> fileUploadService.AliyunOssFileUpload(file);
            case Constants.LOCAL_FILE_UPLOAD -> fileUploadService.localFileUpload(file);
            default -> throw new ProfileException("无法选择上传方式，请检查文件上传配置信息");
        };
        AjaxResult ajax = new AjaxResult();
        ajax.put("url", fileUrl);
        return ajax;
    }
}
