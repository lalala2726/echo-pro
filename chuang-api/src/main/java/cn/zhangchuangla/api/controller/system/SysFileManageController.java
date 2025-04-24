package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.storage.converter.StorageConverter;
import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.manage.SysFileManagementListRequest;
import cn.zhangchuangla.storage.model.vo.manage.StorageFileManagementListVo;
import cn.zhangchuangla.storage.service.StorageManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件资源管理
 *
 * @author Chuang
 * <p>
 * created on 2025/4/8 16:25
 */
@Tag(name = "文件资源")
@RestController
@RequestMapping("/file/manage")
@RequiredArgsConstructor
public class SysFileManageController extends BaseController {

    private final StorageManagementService storageManagementService;
    private final StorageConverter storageConverter;

    /**
     * 文件资源列表
     *
     * @param request 文件资源列表查询参数
     * @return 文件资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "文件资源列表")
    @PreAuthorize("@ss.hasPermission('system:file-manage:list')")
    public AjaxResult listFileManage(@Parameter(description = "文件资源列表查询参数")
                                     @Validated @ParameterObject SysFileManagementListRequest request) {
        Page<SysFileManagement> sysFileManagementPage = storageManagementService.listFileManage(request);
        ArrayList<StorageFileManagementListVo> storageFileManagementListVos = new ArrayList<>();
        sysFileManagementPage.getRecords().forEach(sysFileManagement -> {
            StorageFileManagementListVo storageFileManagementListVo = storageConverter
                    .toSysFileManagementListVo(sysFileManagement);
            storageFileManagementListVo.setIsIncludePreviewImage(!sysFileManagement.getPreviewImageUrl().isEmpty());
            storageFileManagementListVos.add(storageFileManagementListVo);
        });
        return getTableData(sysFileManagementPage, storageFileManagementListVos);
    }

    /**
     * 文件资源回收站列表
     *
     * @param request 文件资源回收站查询参数
     * @return 文件资源回收站列表
     */
    @GetMapping("/trash/list")
    @Operation(summary = "文件资源回收站列表")
    @PreAuthorize("@ss.hasPermission('system:file-manage:list')")
    public AjaxResult listFileTrash(@Parameter(description = "文件资源回收站查询参数")
                                    @Validated @ParameterObject SysFileManagementListRequest request) {
        Page<SysFileManagement> sysFileManagementPage = storageManagementService.listFileTrash(request);
        List<StorageFileManagementListVo> storageFileManagementListVos = copyListProperties(sysFileManagementPage,
                StorageFileManagementListVo.class);
        return getTableData(sysFileManagementPage, storageFileManagementListVos);
    }

    /**
     * 恢复文件
     *
     * @param id 文件ID
     * @return 恢复结果
     */
    // todo 对文件进行操作的时候当前配置必须和文件上传配置一致才可以进行修改，如果不一致则不允许修改
    @PreAuthorize("@ss.hasPermission('system:file-manage:recover')")
    @Operation(summary = "恢复文件")
    @PutMapping("/recover/{id}")
    @OperationLog(title = "文件资源", businessType = BusinessType.RECOVER)
    public AjaxResult recoverFile(@Parameter(description = "文件ID") @PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "文件ID不能为空!");
        boolean result = storageManagementService.recoverFile(id);
        return toAjax(result);
    }

    /**
     * 删除文件,支持批量删除，因为涉及IO操作所以最大支持批量删除100个文件
     *
     * @param ids           文件ID集合
     * @param isPermanently 是否永久删除
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermission('ststem:file-manage:list')")
    @Operation(summary = "删除文件")
    @OperationLog(title = "文件资源", businessType = BusinessType.DELETE)
    public AjaxResult deleteFile(@Parameter(description = "文件ID集合，支持批量删除") @PathVariable("ids") List<Long> ids,
                                 @Parameter(description = "是否永久删除") @RequestParam("isPermanently") Boolean isPermanently) {
        if (isPermanently == null)
            return error("是否删除文件不能为空！");
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "文件ID不能为空!");
        });
        checkParam(ids.size() > 100, "最多只能删除100个文件!");
        boolean result = storageManagementService.removeFile(ids, isPermanently);
        return toAjax(result);
    }
}
