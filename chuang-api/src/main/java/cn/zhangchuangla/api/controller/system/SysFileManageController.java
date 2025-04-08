package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.manage.SysFileManagementListRequest;
import cn.zhangchuangla.storage.model.vo.manage.SysFileManagementListVo;
import cn.zhangchuangla.storage.service.SysFileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件资源管理
 *
 * @author Chuang
 * <p>
 * created on 2025/4/8 16:25
 */
@Tag(name = "文件资源管理")
@RestController
@RequestMapping("/file/manage")
public class SysFileManageController extends BaseController {


    private final SysFileManagementService sysFileManagementService;

    public SysFileManageController(SysFileManagementService sysFileManagementService) {
        this.sysFileManagementService = sysFileManagementService;
    }

    /**
     * 文件资源列表
     *
     * @param request 请求参数
     * @return 文件资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "文件资源列表")
    @PreAuthorize("@ss.hasPermission('system:file-manage:list')")
    public TableDataResult listFileManage(SysFileManagementListRequest request) {
        Page<SysFileManagement> sysFileManagementPage = sysFileManagementService.listFileManage(request);
        List<SysFileManagementListVo> sysFileManagementListVos = copyListProperties(sysFileManagementPage, SysFileManagementListVo.class);
        return getTableData(sysFileManagementPage, sysFileManagementListVos);
    }


    /**
     * 删除文件,支持批量删除，因为涉及IO操作所以最大支持批量删除100个文件
     *
     * @param ids      文件ID
     * @param isDelete 如果是true,则删除文件,如果是false,会暂时删除文件,但文件信息不会被删除
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermission('ststem:file-manage:list')")
    @Operation(summary = "删除文件")
    @OperationLog(title = "文件资源管理", businessType = BusinessType.DELETE)
    public AjaxResult removeFile(@PathVariable("ids") List<Long> ids, @RequestParam("isDelete") Boolean isDelete) {
        if (isDelete == null) return error("是否删除文件不能为空！");
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "文件ID不能为空!");
        });
        checkParam(ids.size() > 100, "最多只能删除100个文件!");
        boolean result = sysFileManagementService.removeFile(ids, isDelete);
        return toAjax(result);
    }
}
