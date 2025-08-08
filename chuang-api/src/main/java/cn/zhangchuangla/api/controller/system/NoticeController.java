package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysNotice;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeAddRequest;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeQueryRequest;
import cn.zhangchuangla.system.core.model.request.notice.SysNoticeUpdateRequest;
import cn.zhangchuangla.system.core.model.vo.notice.SysNoticeListVo;
import cn.zhangchuangla.system.core.model.vo.notice.SysNoticeVo;
import cn.zhangchuangla.system.core.service.SysNoticeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告管理控制器
 *
 * @author Chuang
 */
@Slf4j
@RestController
@RequestMapping("/system/notice")
@Tag(name = "公告管理", description = "提供公告的新增、删除、修改、查询等管理相关接口")
@RequiredArgsConstructor
public class NoticeController extends BaseController {

    private final SysNoticeService sysNoticeService;
    private final ExcelExporter excelExporter;

    /**
     * 公告列表
     *
     * @param request 公告列表查询参数
     * @return 返回公告列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取公告列表", description = "分页查询公告列表")
    @PreAuthorize("@ss.hasPermission('system:notice:list')")
    public AjaxResult<TableDataResult> list(@Parameter(description = "公告查询参数")
                                            @Validated @ParameterObject SysNoticeQueryRequest request) {
        Page<SysNotice> page = sysNoticeService.listNotice(request);
        List<SysNoticeListVo> noticeListVos = copyListProperties(page, SysNoticeListVo.class);

        return getTableData(page, noticeListVos);
    }

    /**
     * 获取公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取公告详情", description = "根据ID获取公告详细信息")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public AjaxResult<SysNoticeVo> getInfo(@Parameter(description = "公告ID") @PathVariable("id") Long id) {
        Assert.notNull(id, "公告ID不能为空");
        SysNotice sysNotice = sysNoticeService.getNoticeById(id);
        SysNoticeVo sysNoticeVo = BeanCotyUtils.copyProperties(sysNotice, SysNoticeVo.class);
        return success(sysNoticeVo);
    }

    /**
     * 新增公告
     *
     * @param request 公告信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增公告", description = "新增公告信息")
    @PreAuthorize("@ss.hasPermission('system:notice:add')")
    @OperationLog(title = "公告管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> add(@Parameter(description = "公告信息") @Validated @RequestBody SysNoticeAddRequest request) {
        boolean result = sysNoticeService.addNotice(request);
        return result ? success("新增成功") : error("新增失败");
    }

    /**
     * 修改公告
     *
     * @param request 公告信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "修改公告", description = "修改公告信息")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    @OperationLog(title = "公告管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> edit(@Parameter(description = "公告信息") @Validated @RequestBody SysNoticeUpdateRequest request) {
        boolean result = sysNoticeService.updateNotice(request);
        return result ? success("修改成功") : error("修改失败");
    }

    /**
     * 删除公告
     *
     * @param ids 公告ID列表
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除公告", description = "批量删除公告")
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    @OperationLog(title = "公告管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> remove(@Parameter(description = "公告ID列表，多个用逗号分隔") @PathVariable List<Long> ids) {
        Assert.notEmpty(ids, "公告ID不能为空");
        boolean result = sysNoticeService.deleteNotice(ids);
        return result ? success("删除成功") : error("删除失败");
    }


    /**
     * 导出公告
     *
     * @param request  查询参数
     * @param response 响应
     */
    @PostMapping("/export")
    @Operation(summary = "导出公告", description = "导出公告")
    @PreAuthorize("@ss.hasPermission('system:notice:export')")
    @OperationLog(title = "公告管理", businessType = BusinessType.EXPORT)
    public void exportNoticeList(@Parameter(description = "查询参数") @RequestBody SysNoticeQueryRequest request, HttpServletResponse response) {
        List<SysNotice> list = sysNoticeService.exportNoticeList(request);
        List<SysNoticeListVo> sysNoticeListVos = copyListProperties(list, SysNoticeListVo.class);
        excelExporter.exportExcel(response, sysNoticeListVos, SysNoticeListVo.class, "公告列表");
    }


}
