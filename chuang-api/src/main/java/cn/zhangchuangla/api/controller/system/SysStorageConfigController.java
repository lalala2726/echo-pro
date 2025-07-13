package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.core.utils.StrUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.StorageRegistryService;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.entity.config.AliyunOSSStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.TencentCOSStorageConfig;
import cn.zhangchuangla.storage.model.request.config.StorageConfigQueryRequest;
import cn.zhangchuangla.storage.model.request.file.UnifiedStorageConfigRequest;
import cn.zhangchuangla.storage.model.vo.config.StorageFileConfigListVo;
import cn.zhangchuangla.storage.service.StorageConfigService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * Created on 2025/4/3 21:39
 */
@RestController
@RequestMapping("/system/file/config")
@RequiredArgsConstructor
@Tag(name = "文件存储配置", description = "提供本地存储、MinIO、阿里云OSS、腾讯云COS等文件存储配置的增删改查及主配置设置、缓存刷新等相关接口")
public class SysStorageConfigController extends BaseController {

    private final StorageConfigService storageConfigService;
    private final StorageRegistryService storageRegistryService;

    /**
     * 文件配置列表
     * 这边转换的值一定要和保存的类一致 {@link cn.zhangchuangla.system.service.impl.SysConfigServiceImpl}
     *
     * @param request 文件配置列表查询参数
     * @return 文件配置列表
     */
    @Operation(summary = "文件配置列表", description = "文件配置列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:storage-config:list')")
    public AjaxResult<TableDataResult> listSysFileConfig(@Parameter(description = "文件配置列表查询参数")
                                                         @Validated @ParameterObject StorageConfigQueryRequest request) {
        Page<StorageConfig> sysFileConfigPage = storageConfigService.listSysFileConfig(request);
        List<StorageFileConfigListVo> storageFileConfigListVos = sysFileConfigPage.getRecords().stream().map(item -> {
            StorageFileConfigListVo storageFileConfigListVo = new StorageFileConfigListVo();
            BeanUtils.copyProperties(item, storageFileConfigListVo);
            switch (item.getStorageType()) {
                case StorageConstants.StorageType.ALIYUN_OSS -> storageFileConfigListVo.setAliyunOssConfig(
                        JSON.parseObject(item.getStorageValue(), AliyunOSSStorageConfig.class));
                case StorageConstants.StorageType.MINIO -> storageFileConfigListVo.setMinioConfig(
                        JSON.parseObject(item.getStorageValue(), MinioStorageConfig.class));
                case StorageConstants.StorageType.TENCENT_COS -> storageFileConfigListVo.setTencentCosConfig(
                        JSON.parseObject(item.getStorageValue(), TencentCOSStorageConfig.class));
            }
            return storageFileConfigListVo;
        }).collect(Collectors.toList());
        return getTableData(sysFileConfigPage, storageFileConfigListVos);
    }

    /**
     * 新增存储配置
     * 支持所有存储类型：MinIO、阿里云OSS、腾讯云COS、亚马逊S3
     *
     * @param request 统一存储配置请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增存储配置", description = "支持所有存储类型的统一配置接口")
    @PreAuthorize("@ss.hasPermission('system:storage-config:add')")
    @PostMapping("/add")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, saveRequestData = false)
    public AjaxResult<Void> addStorageConfig(@Parameter(description = "统一存储配置请求参数")
                                             @Validated @RequestBody UnifiedStorageConfigRequest request) {
        // 去除末尾的斜杠,确保一致性
        if (!StrUtils.isEmpty(request.getEndpoint())) {
            request.setEndpoint(StrUtils.removeTrailingSlash(request.getEndpoint()));
        }
        if (!StrUtils.isEmpty(request.getFileDomain())) {
            request.setFileDomain(StrUtils.removeTrailingSlash(request.getFileDomain()));
        }
        // 对于腾讯云COS，如果region为空，则使用endpoint的值
        if (request.getStorageType() == cn.zhangchuangla.storage.enums.StorageType.TENCENT_COS
                && StrUtils.isEmpty(request.getRegion())) {
            request.setRegion(request.getEndpoint());
        }

        boolean result = storageConfigService.addStorageConfig(request);
        return toAjax(result);
    }

    /**
     * 设置主配置
     *
     * @param id 文件配置ID
     * @return 设置结果
     */
    @PutMapping("/setMaster/{id}")
    @Operation(summary = "设置主配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:update')")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, saveRequestData = false)
    public AjaxResult<Void> updatePrimaryConfig(@Parameter(description = "文件配置ID")
                                                @PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "文件配置ID不能为空!");
        boolean result = storageConfigService.updatePrimaryConfig(id);
        // 刷新缓存
        if (result) {
            refreshCache();
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
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除文件配置")
    @PreAuthorize("@ss.hasPermission('system:storage-config:delete')")
    @OperationLog(title = "文件配置", businessType = BusinessType.DELETE, saveRequestData = false)
    public AjaxResult<Void> deleteFileConfig(@Parameter(description = "文件配置ID集合，支持批量删除")
                                             @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "文件配置ID不能为空!");
        });
        boolean result = storageConfigService.deleteFileConfig(ids);
        return toAjax(result);
    }

}
