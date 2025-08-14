package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysPost;
import cn.zhangchuangla.system.core.model.request.post.SysPostAddRequest;
import cn.zhangchuangla.system.core.model.request.post.SysPostQueryRequest;
import cn.zhangchuangla.system.core.model.request.post.SysPostUpdateRequest;
import cn.zhangchuangla.system.core.model.vo.post.SysPostListVo;
import cn.zhangchuangla.system.core.model.vo.post.SysPostVo;
import cn.zhangchuangla.system.core.service.SysPostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/25 20:30
 */
@Slf4j
@RestController
@RequestMapping("/system/post")
@Tag(name = "岗位管理", description = "提供岗位的新增、删除、修改、查询、批量操作、导出等管理相关接口")
@RequiredArgsConstructor
public class PostController extends BaseController {

    private final SysPostService sysPostService;
    private final ExcelExporter excelExporter;

    /**
     * 岗位列表
     *
     * @param request 岗位列表查询参数
     * @return 返回岗位列表
     */
    @GetMapping("/list")
    @Operation(summary = "岗位列表")
    @PreAuthorize("@ss.hasPermission('system:post:list')")
    public AjaxResult<TableDataResult> listPost(
            @Parameter(description = "岗位列表查询参数") @Validated @ParameterObject SysPostQueryRequest request) {
        Page<SysPost> page = sysPostService.listPost(request);
        List<SysPostListVo> sysPostListVos = copyListProperties(page, SysPostListVo.class);
        return getTableData(page, sysPostListVos);
    }

    /**
     * 添加岗位
     *
     * @param request 添加岗位请求参数
     * @return 操作结果
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:post:add')")
    @Operation(summary = "添加岗位")
    @OperationLog(title = "岗位管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addPost(
            @Parameter(description = "添加岗位请求参数") @Validated @RequestBody SysPostAddRequest request) {
        boolean result = sysPostService.addPost(request);
        return toAjax(result);
    }

    /**
     * 删除岗位,支持批量删除
     *
     * @param ids 岗位ID集合，支持批量删除
     * @return 操作结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    @OperationLog(title = "岗位管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除岗位")
    public AjaxResult<Void> deletePost(@Parameter(description = "岗位ID集合，支持批量删除") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "岗位ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "岗位ID必须大于0！");
        boolean result = sysPostService.deletePost(ids);
        return toAjax(result);
    }

    /**
     * 修改岗位
     *
     * @param request 修改岗位请求参数
     * @return 操作结果
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:post:update')")
    @OperationLog(title = "岗位管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改岗位")
    public AjaxResult<Void> updatePost(@Parameter(description = "修改岗位请求参数") @Validated @RequestBody SysPostUpdateRequest request) {
        return toAjax(sysPostService.updatePost(request));
    }

    /**
     * 获取岗位下拉选项
     *
     * @return 岗位下拉选项
     */
    @GetMapping("/options")
    @Operation(summary = "获取岗位下拉选项")
    public AjaxResult<List<Option<Long>>> getPostOptions() {
        List<Option<Long>> options = sysPostService.getPostOptions();
        return success(options);
    }

    /**
     * 查询岗位
     *
     * @param id 岗位ID
     * @return 操作结果
     */
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    @Operation(summary = "查询岗位")
    public AjaxResult<SysPostVo> getPostById(@Parameter(description = "岗位ID") @PathVariable("id") Long id) {
        Assert.isPositive(id, "岗位ID必须大于0！");
        SysPost post = sysPostService.getPostById(id);
        SysPostVo sysPostVo = new SysPostVo();
        BeanUtils.copyProperties(post, sysPostVo);
        return AjaxResult.success(sysPostVo);
    }

    /**
     * 导出岗位数据
     *
     * @param request  查询参数
     * @param response 响应
     */
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:post:export')")
    @Operation(summary = "导出岗位")
    @OperationLog(title = "岗位管理", businessType = BusinessType.EXPORT)
    public void exportPost(@Parameter(description = "添加查询参数可导出指定查询的数据,为空则导出全部数据")
                           @RequestBody SysPostQueryRequest request, HttpServletResponse response) {
        List<SysPost> sysPosts = sysPostService.exportPostList(request);
        List<SysPostListVo> sysPostListVos = copyListProperties(sysPosts, SysPostListVo.class);
        excelExporter.exportExcel(response, sysPostListVos, SysPostListVo.class, "岗位列表");
    }
}
