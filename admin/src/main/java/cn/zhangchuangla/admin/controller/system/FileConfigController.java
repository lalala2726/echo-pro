package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.constant.SystemMessageConstant;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.DefaultFileUploadEnum;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PathUtils;
import cn.zhangchuangla.system.model.request.file.DefaultFileConfigRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 文件管理控制器
 * <p>
 * created on 2025/3/20 16:43
 */
@Slf4j
@RestController
@RequestMapping("/system/file/config")
@Tag(name = "文件配置")
public class FileConfigController extends BaseController {

    private final RedisCache redisCache;
    private final ConfigCacheService configCacheService;

    public FileConfigController(RedisCache redisCache, ConfigCacheService configCacheService) {
        this.redisCache = redisCache;
        this.configCacheService = configCacheService;
    }

    /**
     * 更新minio配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PutMapping("/config/minio")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "更新Minio配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult updateMinioConfig(@RequestBody @Validated MinioConfigEntity request) {
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO, request);
        configCacheService.refreshAllConfigs();
        return success(SystemMessageConstant.UPDATE_SUCCESS);
    }

    /**
     * 更新阿里云OSS配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PutMapping("/config/oss")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "更新阿里云OSS配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult updateAliyunOSSConfig(@RequestBody @Validated AliyunOSSConfigEntity request) {
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN, request);
        configCacheService.refreshAllConfigs();
        return success(SystemMessageConstant.UPDATE_SUCCESS);
    }

    /**
     * 更新本地文件上传配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PutMapping("/config/local")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "更新本地文件上传配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult updateLocalFileConfig(@RequestBody LocalFileConfigEntity request) {
        String uploadPath = request.getUploadPath();
        log.info("接收前端的地址: {}", uploadPath);

        //  1. 校验路径
        if (PathUtils.isLinuxPath(uploadPath)) {
            log.info("Linux 系统路径，直接上传...");
        } else if (PathUtils.isWindowsPath(uploadPath)) {
            //  2. 处理 Windows 路径
            uploadPath = PathUtils.processWindowsPath(uploadPath);
            request.setUploadPath(uploadPath);
            log.info("处理后的 Windows 地址: {}", uploadPath);
        } else {
            log.error("不支持的路径格式: {}", uploadPath);
            return AjaxResult.error("路径格式不正确，请检查！");
        }

        // 3. 保存到 Redis
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, request);
        log.info("配置已保存至 Redis");
        configCacheService.refreshAllConfigs();
        return AjaxResult.success(SystemMessageConstant.UPDATE_SUCCESS);
    }

    /**
     * 更新默认文件上传配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PutMapping("/config/default")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "更新默认文件上传配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult updateDefaultFileConfig(@RequestBody DefaultFileConfigRequest request) {
        if (request.getFileUploadType().isBlank()) {
            return error("默认文件上传配置不能为空");
        }
        DefaultFileUploadEnum defaultFileUploadEnum = DefaultFileUploadEnum.getByName(request.getFileUploadType());
        if (defaultFileUploadEnum == null) {
            error("您的输入错误，请检查后再输入!");
        }
        // 存储枚举的name值，比如"local", "minio", "oss"

        if (defaultFileUploadEnum != null) {
            redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, defaultFileUploadEnum.getName());
            configCacheService.refreshAllConfigs();
            return success(SystemMessageConstant.UPDATE_SUCCESS);
        }
        return error(SystemMessageConstant.UPDATE_FAIL);
    }

    /**
     * 刷新配置缓存
     */
    @PostMapping("/config/refresh")
    @Log(title = "文件管理", businessType = BusinessType.REFRESH)
    @Operation(summary = "刷新文件配置缓存", description = "当修改文件配置时无法正常指定选择的配置，可以先执行次接口尝试重新刷新尝试")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult refreshConfig() {
        // 现在ProfileException会被正常抛出并由全局异常处理器捕获
        configCacheService.refreshAllConfigs();
        return success("配置刷新成功");
    }


    /**
     * 获取本地文件上传配置
     *
     * @return 本地文件上传配置
     */
    @GetMapping("/config/local")
    @Operation(summary = "获取本地文件上传配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult getLocalFileConfig() {
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        return AjaxResult.success(localFileConfig);
    }

    /**
     * 获取Minio文件上传配置
     *
     * @return Minio文件上传配置
     */
    @GetMapping("/config/minio")
    @Operation(summary = "获取Minio文件上传配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult getMinioConfig() {
        MinioConfigEntity minioConfig = configCacheService.getMinioConfig();
        return AjaxResult.success(minioConfig);
    }

    /**
     * 获取阿里云OSS文件上传配置
     *
     * @return 阿里云OSS文件上传配置
     */
    @GetMapping("/config/oss")
    @Operation(summary = "获取阿里云OSS文件上传配置")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult getAliyunOSSConfig() {
        AliyunOSSConfigEntity aliyunOSSConfig = configCacheService.getAliyunOSSConfig();
        return AjaxResult.success(aliyunOSSConfig);
    }
}
