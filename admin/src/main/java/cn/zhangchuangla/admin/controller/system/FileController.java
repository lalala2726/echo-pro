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
import cn.zhangchuangla.system.model.request.DefaultFileConfigRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 16:43
 */
@RestController
@RequestMapping("/system/file")
@Tag(name = "文件管理")
public class FileController extends BaseController {

    private final RedisCache redisCache;
    private final ConfigCacheService configCacheService;

    public FileController(RedisCache redisCache, ConfigCacheService configCacheService) {
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
    public AjaxResult updateLocalFileConfig(@RequestBody @Validated LocalFileConfigEntity request) {
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, request);
        return success(SystemMessageConstant.UPDATE_SUCCESS);
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
            return error("默认文件上传配置错误");
        }
        // 存储枚举的name值，比如"local", "minio", "oss"
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, defaultFileUploadEnum.getName());
        return success(SystemMessageConstant.UPDATE_SUCCESS);
    }

    /**
     * 刷新配置缓存
     */
    @PostMapping("/config/refresh")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "刷新文件配置缓存")
    @PreAuthorize("@auth.hasPermission('system:file:config')")
    public AjaxResult refreshConfig() {
        configCacheService.refreshAllConfigs();
        return success("配置刷新成功");
    }
}
