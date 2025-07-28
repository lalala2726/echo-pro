package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeUpdateRequest;
import cn.zhangchuangla.system.model.vo.dict.SysDictTypeVo;
import cn.zhangchuangla.system.service.SysDictTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型控制器
 *
 * @author Chuang
 */
@Slf4j
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
@RestController
@Tag(name = "字典类型管理", description = "提供字典类型的增删改查、缓存刷新等相关接口")
public class SysDictTypeController extends BaseController {

    private final SysDictTypeService sysDictTypeService;

    /**
     * 获取字典类型列表
     *
     * @param request 字典类型列表查询参数
     * @return 字典类型列表
     */
    @GetMapping("/list")
    @Operation(summary = "字典类型列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public AjaxResult<TableDataResult> listDictType(@Parameter(description = "字典类型列表查询参数")
                                                    @Validated @ParameterObject SysDictTypeQueryRequest request) {
        Page<SysDictType> sysDictTypePage = sysDictTypeService.listDictType(request);
        List<SysDictTypeVo> sysDictTypeVos = copyListProperties(sysDictTypePage, SysDictTypeVo.class);
        return getTableData(sysDictTypePage, sysDictTypeVos);
    }

    /**
     * 获取字典类型详情
     *
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "字典类型详情")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public AjaxResult<SysDictTypeVo> getDictType(@Parameter(description = "字典类型ID") @PathVariable("id") Long id) {
        Assert.notNull(id, "字典类型ID不能为空！");
        Assert.isTrue(id > 0, "字典类型ID必须大于0！");
        SysDictType sysDictType = sysDictTypeService.getDictTypeById(id);
        SysDictTypeVo sysDictTypeVo = new SysDictTypeVo();
        if (sysDictType != null) {
            BeanUtils.copyProperties(sysDictType, sysDictTypeVo);
        }
        return success(sysDictTypeVo);
    }

    /**
     * 添加字典类型
     *
     * @param request 字典类型添加请求参数
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @OperationLog(title = "字典类型", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addDictType(@Parameter(description = "字典类型添加请求参数")
                                        @Validated @RequestBody SysDictTypeAddRequest request) {
        boolean result = sysDictTypeService.addDictType(request);
        return toAjax(result);
    }

    /**
     * 修改字典类型
     *
     * @param request 字典类型修改请求参数
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @OperationLog(title = "字典类型", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateDictType(@Parameter(description = "字典类型修改请求参数")
                                           @Validated @RequestBody SysDictTypeUpdateRequest request) {
        boolean result = sysDictTypeService.updateDictType(request);
        return toAjax(result);
    }

    /**
     * 删除字典类型
     *
     * @param ids 字典类型ID列表
     * @return 删除结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperationLog(title = "字典类型", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteDictType(@Parameter(description = "字典类型ID列表") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "字典类型ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "字典类型ID必须大于0！");
        boolean result = sysDictTypeService.deleteDictType(ids);
        return toAjax(result);
    }

    /**
     * 获取所有字典类型选项
     *
     * @return 所有字典类型选项
     */
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @Operation(summary = "获取所有字典类型选项")
    @GetMapping("/options")
    public AjaxResult<List<Option<String>>> getAllDictType() {
        List<Option<String>> options = sysDictTypeService.getAllDictType();
        return success(options);
    }

    /**
     * 刷新字典缓存
     *
     * @return 操作结果
     */
    @OperationLog(title = "字典类型", businessType = BusinessType.REFRESH)
    @Operation(summary = "刷新字典缓存")
    @PreAuthorize("@ss.hasPermission('system:dict:refreshCache')")
    @PostMapping("/refreshCache")
    public AjaxResult<Void> refreshCache() {
        boolean result = sysDictTypeService.refreshCache();
        return toAjax(result);
    }
}
