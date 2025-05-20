package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysPost;
import cn.zhangchuangla.system.model.request.post.SysPostAddRequest;
import cn.zhangchuangla.system.model.request.post.SysPostQueryRequest;
import cn.zhangchuangla.system.model.request.post.SysPostUpdateRequest;
import cn.zhangchuangla.system.model.vo.post.SysPostListVo;
import cn.zhangchuangla.system.model.vo.post.SysPostVo;
import cn.zhangchuangla.system.service.SysPostService;
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
 * @author Chuang
 * <p>
 * created on 2025/3/25 20:30
 */
@RestController
@RequestMapping("/system/post")
@Tag(name = "岗位管理")
@RequiredArgsConstructor
public class SysPostController extends BaseController {

    private final SysPostService sysPostService;

    /**
     * 岗位列表
     *
     * @param request 岗位列表查询参数
     * @return 返回岗位列表
     */
    @GetMapping("/list")
    @Operation(summary = "岗位列表")
    @PreAuthorize("@ss.hasPermission('system:post:list')")
    public AjaxResult<TableDataResult> listPost(@Parameter(description = "岗位列表查询参数")
                                                    @Validated @ParameterObject SysPostQueryRequest request) {
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
    public AjaxResult<Void> addPost(@Parameter(description = "添加岗位请求参数")
                                    @Validated @RequestBody SysPostAddRequest request) {
        boolean result = sysPostService.addPost(request);
        return toAjax(result);
    }

    /**
     * 删除岗位,支持批量删除
     *
     * @param ids 岗位ID集合，支持批量删除
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermission('system:post:remove')")
    @OperationLog(title = "岗位管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除岗位")
    public AjaxResult<Void> deletePost(@Parameter(description = "岗位ID集合，支持批量删除")
                                       @PathVariable("ids") List<Long> ids) {
        checkParam(ids == null, "id不能为空");
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
    public AjaxResult<Void> updatePost(@Parameter(description = "修改岗位请求参数")
                                       @Validated @RequestBody SysPostUpdateRequest request) {
        return toAjax(sysPostService.updatePost(request));
    }

    /**
     * 查询岗位
     *
     * @param id 岗位ID
     * @return 操作结果
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    @Operation(summary = "查询岗位")
    public AjaxResult<SysPostVo> getPostById(@Parameter(description = "岗位ID")
                                             @PathVariable("id") Long id) {
        checkParam(id == null, "id不能为空");
        SysPost post = sysPostService.getPostById(id);
        SysPostVo sysPostVo = new SysPostVo();
        BeanUtils.copyProperties(post, sysPostVo);
        return AjaxResult.success(sysPostVo);
    }
}
