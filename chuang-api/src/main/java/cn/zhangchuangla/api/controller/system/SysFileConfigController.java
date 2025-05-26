package cn.zhangchuangla.api.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.core.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.core.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.core.model.request.AliyunOSSConfigRequest;
import cn.zhangchuangla.common.core.model.request.MinioConfigRequest;
import cn.zhangchuangla.common.core.model.request.TencentCOSConfigRequest;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.core.utils.StringUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.storage.loader.StorageConfigLoader;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.request.config.StorageConfigQueryRequest;
import cn.zhangchuangla.storage.model.vo.config.StorageFileConfigListVo;
import cn.zhangchuangla.storage.service.StorageConfigService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * Created on 2025/4/3 21:39
 */
@RestController
@RequestMapping("/system/file/config")
@RequiredArgsConstructor
@Tag(name = "文件存储配置", description = "提供本地存储、MinIO、阿里云OSS、腾讯云COS等文件存储配置的增删改查及主配置设置、缓存刷新等相关接口")
public class SysFileConfigController extends BaseController {

    private final StorageConfigService storageConfigService;
    private final StorageConfigLoader sysFileConfigLoader;

    /**
     * 文件配置列表
     * 这边转换的值一定要和保存的类一致 {@link cn.zhangchuangla.system.service.impl.SysConfigServiceImpl}
     *
     * @param request 文件配置列表查询参数
     * @return 文件配置列表
     */
    @Operation(summary = "文件配置列表", description = "文件配置列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:file-config:list')")
    public AjaxResult<TableDataResult> listSysFileConfig(@Parameter(description = "文件配置列表查询参数")
                                                         @Validated @ParameterObject StorageConfigQueryRequest request) {
        Page<StorageConfig> sysFileConfigPage = storageConfigService.listSysFileConfig(request);
        List<StorageFileConfigListVo> storageFileConfigListVos = sysFileConfigPage.getRecords().stream().map(item -> {
            StorageFileConfigListVo storageFileConfigListVo = new StorageFileConfigListVo();
            BeanUtil.copyProperties(item, storageFileConfigListVo);
            switch (item.getStorageType()) {
                case StorageConstants.ALIYUN_OSS -> storageFileConfigListVo.setAliyunOSSConfig(
                        JSON.parseObject(item.getStorageValue(), AliyunOSSConfigEntity.class));
                case StorageConstants.MINIO -> storageFileConfigListVo.setMinioConfig(
                        JSON.parseObject(item.getStorageValue(), MinioConfigEntity.class));
                case StorageConstants.TENCENT_COS -> storageFileConfigListVo.setTencentCOSConfig(
                        JSON.parseObject(item.getStorageValue(), TencentCOSConfigEntity.class));
            }
            return storageFileConfigListVo;
        }).collect(Collectors.toList());
        return getTableData(sysFileConfigPage, storageFileConfigListVos);
    }

    /**
     * 新增Minio配置
     *
     * @param request Minio配置请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增Minio配置")
    @PreAuthorize("@ss.hasPermission('system:file-config:add')")
    @PostMapping("/add/minio")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult<Void> saveMinioConfig(@Parameter(description = "Minio配置请求参数")
                                            @Validated @RequestBody MinioConfigRequest request) {
        // 去除末尾的斜杠,确保一致性
        String endpoint = request.getEndpoint();
        request.setEndpoint(StringUtils.removeTrailingSlash(endpoint));
        if (!StringUtils.isEmpty(request.getFileDomain())) {
            request.setFileDomain(StringUtils.removeTrailingSlash(request.getFileDomain()));
        }
        boolean result = storageConfigService.saveFileConfig(request);

        return toAjax(result);
    }


    /**
     * 新增阿里云OSS配置
     *
     * @param request 阿里云OSS配置请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增阿里云OSS配置")
    @PreAuthorize("@ss.hasPermission('system:file-config:add')")
    @PostMapping("/add/aliyun")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult<Void> saveAliyunOssConfig(@Parameter(description = "阿里云OSS配置请求参数")
                                                @Validated @RequestBody AliyunOSSConfigRequest request) {
        // 去除末尾的斜杠,确保一致性
        String endpoint = request.getEndpoint();
        request.setEndpoint(StringUtils.removeTrailingSlash(endpoint));
        if (!StringUtils.isEmpty(request.getFileDomain())) {
            request.setFileDomain(StringUtils.removeTrailingSlash(request.getFileDomain()));
        }
        boolean result = storageConfigService.saveFileConfig(request);
        return toAjax(result);
    }

    /**
     * 新增腾讯云COS配置
     *
     * @param request 腾讯云COS配置请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增腾讯云COS配置")
    @PreAuthorize("@ss.hasPermission('system:file-config:add')")
    @PostMapping("/add/tencent")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult<Void> saveTencentCosConfig(@Parameter(description = "腾讯云COS配置请求参数")
                                                 @Validated @RequestBody TencentCOSConfigRequest request) {
        // 去除末尾的斜杠,确保一致性
        String endpoint = request.getRegion();
        request.setRegion(StringUtils.removeTrailingSlash(endpoint));
        if (!StringUtils.isEmpty(request.getFileDomain())) {
            request.setFileDomain(StringUtils.removeTrailingSlash(request.getFileDomain()));
        }
        boolean result = storageConfigService.saveFileConfig(request);
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
    @PreAuthorize("@ss.hasPermission('system:file-config:update')")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, isSaveRequestData = false)
    public AjaxResult<Void> setIsMasterConfig(@Parameter(description = "文件配置ID")
                                              @PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "文件配置ID不能为空!");
        boolean result = storageConfigService.setMasterConfig(id);
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
    @PreAuthorize("@ss.hasPermission('system:file-config:refreshCache')")
    @Operation(summary = "刷新文件配置缓存", description = "通常情况下当修改文件配置后会自动刷新缓存,但如果需要手动刷新可以使用此接口")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, isSaveRequestData = false)
    public AjaxResult<Void> refreshCache() {
        String currentConfigName = sysFileConfigLoader.refreshCache();
        return AjaxResult.success(currentConfigName);
    }

    /**
     * 删除文件配置
     *
     * @param ids 文件配置ID集合，支持批量删除
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除文件配置")
    @PreAuthorize("@ss.hasPermission('system:file-config:delete')")
    @OperationLog(title = "文件配置", businessType = BusinessType.DELETE, isSaveRequestData = false)
    public AjaxResult<Void> deleteFileConfig(@Parameter(description = "文件配置ID集合，支持批量删除")
                                             @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "文件配置ID不能为空!");
            checkParam(Objects.equals(id, StorageConstants.SYSTEM_DEFAULT_FILE_CONFIG_ID),
                    String.format("ID为 %s 是默认配置！无法删除:",
                            StorageConstants.SYSTEM_DEFAULT_FILE_CONFIG_ID));
        });
        boolean result = storageConfigService.deleteFileConfig(ids);
        return toAjax(result);
    }

}
