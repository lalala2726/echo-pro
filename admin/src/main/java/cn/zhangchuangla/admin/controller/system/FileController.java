package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.constant.SystemMessageConstant;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.DefaultFileUploadEnum;
import cn.zhangchuangla.common.result.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public FileController(RedisCache redisCache) {
        this.redisCache = redisCache;
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
    public AjaxResult updateDefaultFileConfig(@RequestBody @Validated DefaultFileUploadEnum request) {
        String name = request.getName();
        if (name.isBlank()) {
            return error("默认文件上传配置不能为空");
        }
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, request.getName());
        return success(SystemMessageConstant.UPDATE_SUCCESS);
    }
}
