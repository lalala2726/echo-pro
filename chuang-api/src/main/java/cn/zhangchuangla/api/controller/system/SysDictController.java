package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.model.entity.Option;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.core.utils.StringUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.*;
import cn.zhangchuangla.system.model.vo.dict.SysDictItemListVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictItemVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictTypeVo;
import cn.zhangchuangla.system.service.SysDictItemService;
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
 * @author Chuang
 * <p>
 * created on 2025/4/17 13:33
 */
@Slf4j
@RequestMapping("/system/dict")
@RequiredArgsConstructor
@RestController
@Tag(name = "字典管理", description = "提供字典类型与字典项的增删改查、缓存刷新等相关接口。")
public class SysDictController extends BaseController {

    private final SysDictTypeService sysDictTypeService;
    private final SysDictItemService sysDictItemService;

    /**
     * 获取字典类型列表
     *
     * @param request 字典类型列表查询参数
     * @return 字典类型列表
     */
    @GetMapping("/type/list")
    @Operation(summary = "字典类型列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public AjaxResult<TableDataResult> listDictType(@Parameter(description = "字典类型列表查询参数")
                                                    @Validated @ParameterObject SysDictTypeQueryRequest request) {
        Page<SysDictType> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysDictType> sysDictTypePage = sysDictTypeService.listDictType(page, request);
        List<SysDictTypeVo> sysDictTypeVos = copyListProperties(sysDictTypePage, SysDictTypeVo.class);
        return getTableData(sysDictTypePage, sysDictTypeVos);
    }

    /**
     * 获取字典类型详情
     *
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    @GetMapping("/type/{id}")
    @Operation(summary = "字典类型详情")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public AjaxResult<SysDictTypeVo> getDictType(@Parameter(description = "字典类型ID") @PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "字典类型ID不能为空!");
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
    @PostMapping("/type")
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
    @PutMapping("/type")
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
    @DeleteMapping("/type/{ids}")
    @Operation(summary = "删除字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperationLog(title = "字典类型", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteDictType(@Parameter(description = "字典类型ID列表") @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> checkParam(id == null || id <= 0, "字典类型ID不能为空!"));
        boolean result = sysDictTypeService.deleteDictType(ids);
        return toAjax(result);
    }

    /**
     * 获取所有字典类型
     *
     * @return 所有字典类型
     */
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @Operation(summary = "获取所有字典类型")
    @GetMapping("/type/all")
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

    /**
     * 获取字典项列表
     *
     * @param request 字典项列表查询参数
     * @return 字典项列表
     */
    @GetMapping("/item/{dictType}/list")
    @Operation(summary = "字典项列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public AjaxResult<TableDataResult> listDictItem(@PathVariable("dictType") String dictType,
                                                    @Parameter(description = "字典项列表查询参数")
                                                    @Validated @ParameterObject SysDictItemQueryRequest request) {
        if (StringUtils.isBlank(dictType)) {
            return error("字典类型编码不能为空!");
        }
        Page<SysDictItem> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysDictItem> sysDictItemPage = sysDictItemService.listDictItem(page, dictType, request);
        List<SysDictItemListVo> sysDictItemVos = copyListProperties(sysDictItemPage, SysDictItemListVo.class);
        return getTableData(sysDictItemPage, sysDictItemVos);
    }

    /**
     * 获取字典项选项
     *
     * @param dictType 字典类型编码
     * @return 字典项选项
     */
    @GetMapping("/item/option/{dictType}")
    @Operation(summary = "字典项选项")
    public AjaxResult<List<Option<String>>> getDictItemOption(@PathVariable("dictType") String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return error("字典类型编码不能为空!");
        }
        List<Option<String>> options = sysDictItemService.getDictItemOption(dictType);
        return success(options);

    }

    /**
     * 获取字典项详情
     *
     * @param id 字典项ID
     * @return 字典项详情
     */
    @GetMapping("/item/{id}")
    @Operation(summary = "字典项详情")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public AjaxResult<SysDictItemVo> getDictItem(@Parameter(description = "字典项ID") @PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "字典项ID不能为空!");
        SysDictItem sysDictItem = sysDictItemService.getDictItemById(id);
        SysDictItemVo sysDictItemVo = new SysDictItemVo();
        if (sysDictItem != null) {
            BeanUtils.copyProperties(sysDictItem, sysDictItemVo);
        }
        return success(sysDictItemVo);
    }

    /**
     * 添加字典项
     *
     * @param request 字典项添加请求参数
     * @return 添加结果
     */
    @PostMapping("/item")
    @Operation(summary = "添加字典项")
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @OperationLog(title = "字典项", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addDictItem(@Parameter(description = "字典项添加请求参数")
                                        @Validated @RequestBody SysDictItemAddRequest request) {
        // 检查字典类型是否存在
        if (!sysDictTypeService.isDictTypeExist(request.getDictType())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典类型不存在: " + request.getDictType());
        }
        boolean result = sysDictItemService.addDictItem(request);
        return toAjax(result);
    }

    /**
     * 修改字典项
     *
     * @param request 字典项修改请求参数
     * @return 修改结果
     */
    @PutMapping("/item")
    @Operation(summary = "修改字典项")
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @OperationLog(title = "字典项", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateDictItem(@Parameter(description = "字典项修改请求参数")
                                           @Validated @RequestBody SysDictItemUpdateRequest request) {
        boolean result = sysDictItemService.updateDictItem(request);
        return toAjax(result);
    }

    /**
     * 删除字典项
     *
     * @param ids 字典项ID列表
     * @return 删除结果
     */
    @DeleteMapping("/item/{ids}")
    @Operation(summary = "删除字典项")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperationLog(title = "字典项", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteDictItem(@Parameter(description = "字典项ID列表") @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> checkParam(id == null || id <= 0, "字典项ID不能为空!"));
        boolean result = sysDictItemService.deleteDictItem(ids);
        return toAjax(result);
    }
}
