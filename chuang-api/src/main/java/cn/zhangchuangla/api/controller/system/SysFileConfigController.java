package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.model.request.AliyunOSSConfigRequest;
import cn.zhangchuangla.common.model.request.LocalFileConfigRequest;
import cn.zhangchuangla.common.model.request.MinioConfigRequest;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.model.request.file.SysFileConfigAddRequest;
import cn.zhangchuangla.system.model.request.file.SysFileConfigListRequest;
import cn.zhangchuangla.system.model.vo.file.config.SysFileConfigListVo;
import cn.zhangchuangla.system.service.SysFileConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangchuang
 * Created on 2025/4/3 21:39
 */

@RestController
@RequestMapping("/system/file/config")
@Tag(name = "文件配置")
public class SysFileConfigController extends BaseController {


    private final SysFileConfigService sysFileConfigService;

    private final SysFileConfigLoader sysFileConfigLoader;


    @Autowired
    public SysFileConfigController(SysFileConfigService sysFileConfigService, SysFileConfigLoader sysFileConfigLoader) {
        this.sysFileConfigService = sysFileConfigService;
        this.sysFileConfigLoader = sysFileConfigLoader;
    }


    /**
     * 文件配置列表
     *
     * @param request 查询参数
     * @return 文件配置列表
     */
    @Operation(summary = "文件配置列表", description = "文件配置列表")
    @GetMapping("/list")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:list')")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public TableDataResult listSysFileConfig(SysFileConfigListRequest request) {
        Page<SysFileConfig> sysFileConfigPage = sysFileConfigService.listSysFileConfig(request);
        List<SysFileConfigListVo> sysFileConfigListVos = copyListProperties(sysFileConfigPage, SysFileConfigListVo.class);
        return getTableData(sysFileConfigPage, sysFileConfigListVos);
    }


    /**
     * 新增文件配置
     *
     * @param request 文件配置信息
     * @return 新增结果
     */
    @Operation(summary = "新增文件配置", description = "新增文件配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @PostMapping("/add")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult saveFileConfig(@Validated @RequestBody SysFileConfigAddRequest request) {
        boolean result = sysFileConfigService.saveFileConfig(request);
        return toAjax(result);
    }

    /**
     * 新增Minio配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增Minio配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @PostMapping("/add/minio")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult saveMinioConfig(@Validated @RequestBody MinioConfigRequest request) {
        // 去除末尾的斜杠,确保一致性
        String endpoint = request.getEndpoint();
        request.setEndpoint(StringUtils.removeTrailingSlash(endpoint));
        request.setFileDomain(StringUtils.removeTrailingSlash(request.getFileDomain()));
        boolean result = sysFileConfigService.saveFileConfig(request);

        return toAjax(result);
    }


    /**
     * 新增阿里云OSS配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增阿里云OSS配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @PostMapping("/add/aliyun")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult saveAliyunOssConfig(@Validated @RequestBody AliyunOSSConfigRequest request) {
        // 去除末尾的斜杠,确保一致性
        String endpoint = request.getEndpoint();
        request.setEndpoint(StringUtils.removeTrailingSlash(endpoint));
        request.setFileDomain(StringUtils.removeTrailingSlash(request.getFileDomain()));
        boolean result = sysFileConfigService.saveFileConfig(request);
        return toAjax(result);
    }

    /**
     * 新增本地文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增本地配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @PostMapping("/add/local")
    @OperationLog(title = "文件配置", businessType = BusinessType.INSERT, isSaveRequestData = false)
    public AjaxResult saveLocalConfig(LocalFileConfigRequest request) {
        boolean result = sysFileConfigService.saveFileConfig(request);
        return toAjax(result);
    }


    /**
     * 设置主配置
     *
     * @return 设置结果
     */
    @PutMapping("/setMaster/{id}")
    @Operation(summary = "设置主配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:update')")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, isSaveRequestData = false)
    public AjaxResult setIsMasterConfig(@PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "文件配置ID不能为空!");
        boolean result = sysFileConfigService.setMasterConfig(id);
        // 刷新缓存
        if (result) refreshCache();
        return toAjax(result);
    }


    /**
     * 刷新文件配置缓存
     *
     * @return 刷新结果
     */
    @GetMapping("/refreshCache")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:refreshCache')")
    @Operation(summary = "刷新文件配置缓存", description = "通常情况下当修改文件配置后会自动刷新缓存,但如果需要手动刷新可以使用此接口")
    @OperationLog(title = "文件配置", businessType = BusinessType.UPDATE, isSaveRequestData = false)
    public AjaxResult refreshCache() {
        String currentConfigName = sysFileConfigLoader.refreshCache();
        return AjaxResult.success(currentConfigName);
    }

}
