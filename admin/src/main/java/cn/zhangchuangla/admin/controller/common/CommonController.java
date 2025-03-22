package cn.zhangchuangla.admin.controller.common;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.FileUploadResult;
import cn.zhangchuangla.system.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "通用接口")
@RequestMapping("/common")
@RestController
public class CommonController extends BaseController {


    private final FileUploadService fileUploadService;
    private final ConfigCacheService configCacheService;

    @Autowired
    public CommonController(FileUploadService fileUploadService, ConfigCacheService configCacheService) {
        this.fileUploadService = fileUploadService;
        this.configCacheService = configCacheService;
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
            @RequestParam("file") MultipartFile file) {
        try {
            // 验证参数
            if (file == null || file.isEmpty()) {
                return error();
            }
            String defaultFileUploadType = configCacheService.getDefaultFileUploadType();
            // 调用服务上传文件（自动检测图片并压缩）
            FileUploadResult result = fileUploadService.uploadWithImageProcess(file, defaultFileUploadType);
            // 返回结果
            AjaxResult response = AjaxResult.success("文件上传成功");
            response.put("data", result);

            if (result.isImage()) {
                log.info("图片上传成功 - 原始URL: {}, 压缩URL: {}", result.getOriginalUrl(), result.getCompressedUrl());
            } else {
                log.info("文件上传成功 - URL: {}", result.getOriginalUrl());
            }
            return response;
        } catch (ServiceException e) {
            log.error("文件上传服务异常: {}", e.getMessage());
            return error(e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return error("文件上传失败！" + e.getMessage());
        }
    }


}
