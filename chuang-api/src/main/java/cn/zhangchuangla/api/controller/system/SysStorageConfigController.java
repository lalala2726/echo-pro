package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.excel.utils.ExcelUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.storage.core.service.StorageRegistryService;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.request.config.*;
import cn.zhangchuangla.storage.model.vo.config.StorageConfigListVo;
import cn.zhangchuangla.storage.model.vo.config.StorageConfigUnifiedVo;
import cn.zhangchuangla.storage.service.StorageConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * Created on 2025/4/3 21:39
 */
@RestController
@RequestMapping("/system/storage/config")
@RequiredArgsConstructor
@Tag(name = "文件存储配置", description = "提供本地存储、MinIO、阿里云OSS、腾讯云COS等文件存储配置的增删改查及主配置设置、缓存刷新等相关接口")
public class SysStorageConfigController extends BaseController {

    private final StorageConfigService storageConfigService;
    private final StorageRegistryService storageRegistryService;
    private final ExcelUtils excelUtils;


    /**
     * 文件配置列表
     *
     * @param request 文件配置列表查询参数
     * @return 文件配置列表
     */
    @Operation(summary = "文件配置列表", description = "文件配置列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:storage-config:list')")
    public AjaxResult<TableDataResult> listSysFileConfig(@Parameter(description = "文件配置列表查询参数")
                                                             @ParameterObject StorageConfigQueryRequest request) {
        Page<StorageConfig> sysFileConfigPage = storageConfigService.listSysFileConfig(request);
        List<StorageConfigListVo> storageConfigListVos = copyListProperties(sysFileConfigPage, StorageConfigListVo.class);
        return getTableData(sysFileConfigPage, storageConfigListVos);
    }


    /**
     * 导出存储配置
     *
     * @param request  存储配置查询参数
     * @param response 响应对象
     */
    @Operation(summary = "导出存储配置")
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:storage-config:export')")
    public void exportStorageConfig(@ParameterObject StorageConfigQueryRequest request, HttpServletResponse response) {
        List<StorageConfigUnifiedVo> storageConfigUnifiedVos = storageConfigService.listStorageConfig(request);
        excelUtils.exportExcel(response, storageConfigUnifiedVos, StorageConfigUnifiedVo.class, "存储配置列表");
    }


    /**
     * 判断存储配置Key是否存在
     *
     * @param id         存储配置ID
     * @param storageKey 存储配置Key
     * @return true存在，false不存在
     */
    @GetMapping("/key-exists")
    public AjaxResult<Boolean> isStorageKeyExists(@RequestParam(value = "id", required = false) Long id,
                                                  @RequestParam("storageKey") String storageKey) {
        boolean result = storageConfigService.isStorageKeyExists(id, storageKey);
        return AjaxResult.success(result);
    }

    /**
     * 获取存储配置键选项
     *
     * @return 存储配置键选项
     */
    @GetMapping("/key-option")
    @Operation(summary = "获取存储配置键选项")
    public AjaxResult<List<Option<String>>> getStorageConfigKeyOption() {
        List<Option<String>> options = storageConfigService.getStorageConfigKeyOption();
        return success(options);
    }


    /**
     * 添加Minio存储配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/minio")
    @Operation(summary = "添加Minio存储配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:add')")
    @OperationLog(title = "存储配置", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addMinioConfig(@Validated @RequestBody MinioConfigSaveRequest request) {
        boolean result = storageConfigService.addMinioConfig(request);
        return toAjax(result);
    }

    /**
     * 添加阿里云OSS存储配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/aliyun")
    @Operation(summary = "添加阿里云OSS存储配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:add')")
    @OperationLog(title = "存储配置", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addAliyunOssConfig(@Validated @RequestBody AliyunOssConfigSaveRequest request) {
        boolean result = storageConfigService.addAliyunOssConfig(request);
        return toAjax(result);
    }


    /**
     * 添加腾讯云COS存储配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/tencent")
    @Operation(summary = "添加腾讯云COS存储配置")
    @OperationLog(title = "存储配置", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addTencentCosConfig(@Validated @RequestBody TencentCosConfigSaveRequest request) {
        boolean result = storageConfigService.addTencentCosConfig(request);
        return toAjax(result);
    }

    /**
     * 添加亚马逊S3存储配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/amaze")
    @Operation(summary = "添加亚马逊S3存储配置", description = "兼容S3协议可以使用此接口")
    @OperationLog(title = "存储配置", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addAmazonS3Config(@Validated @RequestBody AmazonS3ConfigSaveRequest request) {
        boolean result = storageConfigService.addAmazonS3Config(request);
        return toAjax(result);
    }


    /**
     * 根据ID查询文件配置
     *
     * @param id 文件配置ID
     * @return 文件配置
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取存储配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:query')")
    public AjaxResult<StorageConfigUnifiedVo> getStorageConfigById(@PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "存储配置ID必须大于0！");
        StorageConfigUnifiedVo storageConfig = storageConfigService.getStorageConfigById(id);
        return success(storageConfig);
    }

    /**
     * 修改存储配置
     *
     * @param request 修改存储配置请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改存储配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:update')")
    @OperationLog(title = "存储配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult<Void> updateStorageConConfig(@Validated @RequestBody StorageConfigUpdateRequest request) {
        boolean result = storageConfigService.updateStorageConConfig(request);
        return toAjax(result);
    }

    /**
     * 设置主配置
     *
     * @param id 文件配置ID
     * @return 设置结果
     */
    @PutMapping("/primary/{id:\\d+}")
    @Operation(summary = "设置主配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:update')")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, saveRequestData = false)
    public AjaxResult<Void> updatePrimaryConfig(@Parameter(description = "文件配置ID")
                                                @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "文件配置ID必须大于0！");
        boolean result = storageConfigService.updatePrimaryConfig(id);
        // 刷新缓存
        if (result) {
            storageRegistryService.initializeStorage();
        }
        return toAjax(result);
    }

    /**
     * 刷新文件配置缓存
     *
     * @return 刷新结果
     */
    @PutMapping("/refreshCache")
    @PreAuthorize("@ss.hasPermission('system:storage-config:refreshCache')")
    @Operation(summary = "刷新文件配置缓存", description = "通常情况下当修改文件配置后会自动刷新缓存,但如果需要手动刷新可以使用此接口")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, saveRequestData = false)
    public AjaxResult<Void> refreshCache() {
        storageRegistryService.initializeStorage();
        return success();
    }

    /**
     * 删除文件配置
     *
     * @param ids 文件配置ID集合，支持批量删除
     * @return 删除结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除文件配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:delete')")
    @OperationLog(title = "文件配置", businessType = BusinessType.DELETE, saveRequestData = false)
    public AjaxResult<Void> deleteFileConfig(@Parameter(description = "文件配置ID集合，支持批量删除")
                                             @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "文件配置ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "文件配置ID必须大于0！");
        boolean result = storageConfigService.deleteFileConfig(ids);
        return toAjax(result);
    }

}
