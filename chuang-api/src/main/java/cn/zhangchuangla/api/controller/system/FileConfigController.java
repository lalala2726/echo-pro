package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.model.request.file.SysFileConfigAddRequest;
import cn.zhangchuangla.system.model.request.file.SysFileConfigListRequest;
import cn.zhangchuangla.system.model.vo.file.manage.FileManagementListVo;
import cn.zhangchuangla.system.service.SysFileConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangchuang
 * Created on 2025/4/3 21:39
 */

@RestController
@RequestMapping("/system/file/config")
@Tag(name = "文件配置")
public class FileConfigController extends BaseController {


    private final SysFileConfigService sysFileConfigService;


    @Autowired
    public FileConfigController(SysFileConfigService sysFileConfigService) {
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
        List<FileManagementListVo> fileManagementListVos = copyListProperties(sysFileConfigPage, FileManagementListVo.class);
        return getTableData(sysFileConfigPage, fileManagementListVos);
    }


    /**
     * 新增文件配置
     *
     * @param request 文件配置信息
     * @return 新增结果
     */
    @Operation(summary = "新增文件配置", description = "新增文件配置")
    @PreAuthorize("@auth.hasAnyPermission('system:file-config:add')")
    @GetMapping("/add")
    public AjaxResult saveFileConfig(SysFileConfigAddRequest request) {
        boolean result = sysFileConfigService.saveFileConfig(request);
        return toAjax(result);
    }
}
