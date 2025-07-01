package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.storage.model.request.file.SysFileQueryRequest;
import cn.zhangchuangla.storage.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件资源管理
 *
 * @author Chuang
 * <p>
 * created on 2025/4/8 16:25
 */
@RestController
@Tag(name = "文件资源管理", description = "提供文件资源的列表查询、回收站管理、恢复、删除等相关操作接口")
@RequestMapping("/system/file/manage")
@RequiredArgsConstructor
public class SysFileManageController extends BaseController {

    private final StorageService storageService;


    /**
     * 文件资源列表
     *
     * @param request 文件资源列表查询参数
     * @return 文件资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "文件资源列表")
    @PreAuthorize("@ss.hasPermission('system:file-manage:list')")
    public AjaxResult<TableDataResult> listFileManage(@Parameter(description = "文件资源列表查询参数")
                                                      @Validated @ParameterObject SysFileQueryRequest request) {

        return success();
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
    public AjaxResult<TableDataResult> listFileTrash(@Parameter(description = "文件资源回收站查询参数")
                                                     @Validated @ParameterObject SysFileQueryRequest request) {
        return success();
    }

    /**
     * 恢复文件
     *
     * @param ids 文件ID
     * @return 恢复结果
     */
    // todo 对文件进行操作的时候当前配置必须和文件上传配置一致才可以进行修改，如果不一致则不允许修改
    @PreAuthorize("@ss.hasPermission('system:file-manage:recover')")
    @Operation(summary = "恢复文件")
    @PutMapping("/recover/{ids}")
    @OperationLog(title = "文件资源", businessType = BusinessType.RECOVER)
    public AjaxResult<Void> recoverFile(@Parameter(description = "文件ID") @PathVariable("ids") List<Long> ids) {
        boolean result = storageService.restoreFileFromRecycleBin(ids);
        return toAjax(result);
    }

    /**
     * 删除文件,支持批量删除，因为涉及IO操作所以最大支持批量删除100个文件
     *
     * @param ids         文件ID集合
     * @param forceDelete 是否永久删除
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermission('ststem:file-manage:list')")
    @Operation(summary = "删除文件")
    @OperationLog(title = "文件资源", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteFile(@Parameter(description = "文件ID集合，支持批量删除") @PathVariable("ids") List<Long> ids,
                                       @Parameter(description = "是否永久删除")
                                       @RequestParam(value = "forceDelete", required = false, defaultValue = "false") boolean forceDelete) {
        boolean result = storageService.deleteFileById(ids, forceDelete);
        return toAjax(result);
    }
}
