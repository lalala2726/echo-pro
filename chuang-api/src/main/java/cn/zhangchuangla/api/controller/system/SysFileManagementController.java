package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.model.request.file.FileManagementListRequest;
import cn.zhangchuangla.system.model.vo.file.FileManagementListVo;
import cn.zhangchuangla.system.service.FileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangchuang
 * Created on 2025/3/22 13:11
 */
@RestController
@RequestMapping("/system/file/management")
@Tag(name = "文件管理", description = "管理系统所上传的文件")
public class SysFileManagementController extends BaseController {

    private final FileManagementService fileManagementService;

    @Autowired
    public SysFileManagementController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }


    /**
     * 文件列表
     *
     * @param request 文件列表请求参数
     * @return 文件列表分页结果
     */
    @Operation(summary = "文件列表", description = "获取文件列表")
    @PreAuthorize("@auth.hasPermission('system:file:management:list')")
    @GetMapping("/list")
    public TableDataResult fileList(FileManagementListRequest request) {
        Page<FileManagement> page = fileManagementService.fileList(request);
        List<FileManagementListVo> fileManagementListVos = copyListProperties(page, FileManagementListVo.class);
        return getTableData(page, fileManagementListVos);
    }

    /**
     * 删除文件，支持批量删除
     *
     * @param ids 文件id集合
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除文件", description = "会根据文件所处的存储位置进行删除")
    @PreAuthorize("@auth.hasPermission('system:file:management:delete')")
    @Log(title = "文件管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteFile(@PathVariable("ids") List<Long> ids) {
        fileManagementService.deleteFile(ids);
        return success();
    }

    /**
     * 根据文件id获取文件信息
     *
     * @param id 文件id
     * @return 文件信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取文件信息", description = "根据文件id获取文件信息")
    @PreAuthorize("@auth.hasPermission('system:file:management:info')")
    public AjaxResult getFileById(@PathVariable("id") Long id) {
        checkParam(id == null, "文件ID不能为空！");
        FileManagement fileManagement = fileManagementService.getFileById(id);
        return success(fileManagement);
    }
}
