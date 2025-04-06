package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.model.request.AliyunOSSConfigRequest;
import cn.zhangchuangla.common.model.request.LocalFileConfigRequest;
import cn.zhangchuangla.common.model.request.MinioConfigRequest;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.StringUtils;
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


    @Autowired
    public SysFileConfigController(SysFileConfigService sysFileConfigService) {
        this.sysFileConfigService = sysFileConfigService;
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
    @Operation(summary = "新增Minio配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @PostMapping("/add/aliyun")
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
    @Operation(summary = "新增Minio配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @PostMapping("/add/local")
    public AjaxResult saveLocalConfig(LocalFileConfigRequest request) {
        boolean result = sysFileConfigService.saveFileConfig(request);
        return toAjax(result);
    }


}
