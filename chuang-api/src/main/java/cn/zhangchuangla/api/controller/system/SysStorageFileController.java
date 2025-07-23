package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.storage.model.entity.StorageFile;
import cn.zhangchuangla.storage.model.request.file.StorageFileQueryRequest;
import cn.zhangchuangla.storage.model.vo.file.StorageFileListVo;
import cn.zhangchuangla.storage.model.vo.file.StorageFileVo;
import cn.zhangchuangla.storage.service.StorageFileService;
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

/**
 * 文件资源管理
 *
 * @author Chuang
 * <p>
 * created on 2025/4/8 16:25
 */
@RestController
@Tag(name = "文件资源管理", description = "提供文件资源的列表查询、回收站管理、恢复、删除等相关操作接口")
@RequestMapping("/system/storage/file")
@RequiredArgsConstructor
public class SysStorageFileController extends BaseController {

    private final StorageFileService storageFileService;


    /**
     * 文件资源列表
     *
     * @param request 文件资源列表查询参数
     * @return 文件资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "文件资源列表")
    @PreAuthorize("@ss.hasPermission('system:storage-file:list')")
    public AjaxResult<TableDataResult> listFileManage(@Parameter(description = "文件资源列表查询参数")
                                                          @Validated @ParameterObject StorageFileQueryRequest request) {
        Page<StorageFile> page = storageFileService.listFileManage(request);
        List<StorageFileListVo> fileRecordListVos = copyListProperties(page, StorageFileListVo.class);
        return getTableData(page, fileRecordListVos);
    }

    //todo 导出文件资源列表

    /**
     * 文件资源详情
     *
     * @param id 文件ID
     * @return 文件详情
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "文件资源详情")
    @PreAuthorize("@ss.hasPermission('system:storage-file:query')")
    public AjaxResult<StorageFileVo> getFileById(@Parameter(description = "文件ID")
                                                @PathVariable("id") Long id) {
        StorageFile storageFile = storageFileService.getFileById(id);
        StorageFileVo storageFileVo = new StorageFileVo();
        BeanUtils.copyProperties(storageFile, storageFileVo);
        return success(storageFileVo);
    }

    /**
     * 文件资源回收站列表
     *
     * @param request 文件资源回收站查询参数
     * @return 文件资源回收站列表
     */
    @GetMapping("/trash/list")
    @Operation(summary = "文件资源回收站列表")
    @PreAuthorize("@ss.hasPermission('system:storage-file:list')")
    public AjaxResult<TableDataResult> listFileTrash(@Parameter(description = "文件资源回收站查询参数")
                                                         @Validated @ParameterObject StorageFileQueryRequest request) {
        Page<StorageFile> page = storageFileService.listFileTrashManage(request);
        List<StorageFileListVo> fileRecordListVos = copyListProperties(page, StorageFileListVo.class);
        return getTableData(page, fileRecordListVos);
    }

    /**
     * 从回收站删除文件
     *
     * @param ids 文件ID集合
     * @return 操作结果
     */
    @DeleteMapping("/trash/{ids:[\\d,]+}")
    @Operation(summary = "删除回收站文件")
    @OperationLog(title = "文件管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:storage-file:delete')")
    public AjaxResult<Void> deleteTrashFile(@Parameter(description = "文件ID集合，支持批量删除")
                                            @PathVariable("ids") List<Long> ids) {
        boolean result = storageFileService.deleteTrashFileById(ids);
        return toAjax(result);
    }


    /**
     * 恢复文件
     *
     * @param ids 文件ID
     * @return 恢复结果
     */
    @PreAuthorize("@ss.hasPermission('system:storage-file:recover')")
    @Operation(summary = "恢复文件")
    @PutMapping("/recover/{ids:[\\d,]+}")
    @OperationLog(title = "文件资源", businessType = BusinessType.RECOVER)
    public AjaxResult<Void> recoverFile(@Parameter(description = "文件ID") @PathVariable("ids") List<Long> ids) {
        boolean result = storageFileService.restoreFileFromRecycleBin(ids);
        return toAjax(result);
    }

    /**
     * 删除文件,支持批量删除，因为涉及IO操作所以最大支持批量删除100个文件
     *
     * @param ids         文件ID集合
     * @param forceDelete 是否永久删除
     * @return 删除结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @PreAuthorize("@ss.hasPermission('ststem:file:list')")
    @Operation(summary = "删除文件")
    @OperationLog(title = "文件资源", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteFile(@Parameter(description = "文件ID集合，支持批量删除") @PathVariable("ids") List<Long> ids,
                                       @Parameter(description = "是否永久删除")
                                       @RequestParam(value = "forceDelete", required = false, defaultValue = "false") boolean forceDelete) {
        boolean result = storageFileService.deleteFileById(ids, forceDelete);
        return toAjax(result);
    }
}
