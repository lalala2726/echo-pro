package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.converter.SysDictConverter;
import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.*;
import cn.zhangchuangla.system.model.vo.dict.*;
import cn.zhangchuangla.system.service.SysDictItemService;
import cn.zhangchuangla.system.service.SysDictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "字典接口")
public class SysDictController extends BaseController {

    private final SysDictService sysDictService;
    private final SysDictItemService sysDictItemService;
    private final SysDictConverter sysDictConverter;


    /**
     * 获取字典列表
     *
     * @param request 查询参数
     * @return 字典列表
     */
    @Operation(summary = "获取字典列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public AjaxResult listDict(@Parameter(description = "字典列表请求类")
                               @Validated @ParameterObject SysDictListRequest request) {
        Page<SysDict> sysDict = sysDictService.listDict(request);
        List<SysDictListVo> sysDictListVos = copyListProperties(sysDict, SysDictListVo.class);
        return getTableData(sysDict, sysDictListVos);
    }

    /**
     * 新增字典
     *
     * @param request 请求参数
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增字典")
    @OperationLog(title = "字典管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    public AjaxResult addDict(@Parameter(description = "字典添加请求类")
                              @Validated @RequestBody SysDictAddRequest request) {
        // 校验字典编码是否存在
        if (sysDictService.isDictCodeExist(request.getDictCode())) return error("字典编码已存在");
        boolean result = sysDictService.addDict(request);
        return toAjax(result);
    }

    /**
     * 获取字典选项
     *
     * @param id 字典ID
     * @return 字典选项列表
     */
    @Operation(summary = "获取字典选项")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public AjaxResult getDict(@Parameter(description = "字典ID") @PathVariable("id") Long id) {
        SysDict sysDict = sysDictService.getDictById(id);
        SysDictVo sysDictVo = sysDictConverter.toSysDictVo(sysDict);
        return success(sysDictVo);
    }

    /**
     * 删除字典，支持批量删除
     *
     * @param ids 字典ID列表
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典")
    @OperationLog(title = "字典管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    public AjaxResult deleteDict(@Parameter(description = "字典ID，支持支持批量删除，如果删除时候其中一项删除失败，数据将会回滚")
                                     @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> checkParam(id == null || id <= 0, "字典ID不能为空!"));
        // 删除字典
        boolean result = sysDictService.deleteDict(ids);
        return toAjax(result);
    }

    /**
     * 修改字典
     *
     * @param request 请求参数
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改字典")
    @OperationLog(title = "字典管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public AjaxResult updateDict(@Parameter(description = "字典更新请求类")
                                 @Validated @RequestBody SysDictUpdateRequest request) {
        boolean result = sysDictService.updateDict(request);
        return toAjax(result);
    }


    /*------------- 字典项相关接口 -------------- **/


    /**
     * 获取指定字典项分页
     *
     * @param dictCode 字典编码
     * @param request  查询参数
     * @return 字典项列表
     */
    @GetMapping("/items/list/{dictCode}")
    @Operation(summary = "获取字典项分页")
    @PreAuthorize("@ss.hasPermission('system:dict-item:list')")
    public AjaxResult listDictData(@PathVariable("dictCode") @Parameter(description = "字典编码") String dictCode,
                                   @Parameter(description = "字典项列表查询请求类")
                                   @ParameterObject @Validated SysDictItemListRequest request) {
        if (dictCode.isEmpty()) throw new ParamException(ResponseCode.PARAM_NOT_NULL, "字典编码不能为空！");
        Page<SysDictItem> sysDictItemPage = sysDictItemService.listDictData(dictCode, request);
        List<SysDictItemListVo> sysDictItemListVos = copyListProperties(sysDictItemPage, SysDictItemListVo.class);
        return getTableData(sysDictItemPage, sysDictItemListVos);
    }

    /**
     * 获取字典项列表
     *
     * @param request 查询参数
     * @return 字典项列表
     */
    @GetMapping("/items/list")
    @Operation(summary = "获取字典项分页")
    @PreAuthorize("@ss.hasPermission('system:dict-item:list')")
    public AjaxResult listDictData(@Parameter(description = "字典项列表请求类")
                                       @Validated @ParameterObject SysDictItemListRequest request) {
        Page<SysDictItem> sysDictItemPage = sysDictItemService.listDictData(request);
        List<SysDictItemListVo> sysDictItemListVos = copyListProperties(sysDictItemPage, SysDictItemListVo.class);
        return success(getTableData(sysDictItemPage, sysDictItemListVos));
    }

    /**
     * 获取字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @GetMapping("/items/{dictCode}")
    @Operation(summary = "查询字典项列表")
    @PreAuthorize("@ss.hasPermission('system:dict-item:query')")
    public AjaxResult getDictItems(@Parameter(description = "字典编码")
                                   @PathVariable("dictCode") String dictCode) {
        if (dictCode.isEmpty()) return error("字典编码不能为空");
        List<SysDictItem> sysDictItems = sysDictItemService.getDictItems(dictCode);
        return success(sysDictItems);
    }

    /**
     * 获取字典项选项列表
     *
     * @param dictCode 字典编码
     * @return 字典项选项列表
     */
    @GetMapping("/items/options/{dictCode}")
    @Operation(summary = "获取字典项选项")
    public AjaxResult getDictItemOptions(@Parameter(description = "字典编码")
                                         @PathVariable("dictCode") String dictCode) {
        if (dictCode.isEmpty()) return error("字典编码不能为空");
        List<SysDictItem> sysDictItems = sysDictItemService.getDictItemOptionVo(dictCode);
        List<SysDictItemOptionVo> sysDictItemOptionVo = sysDictConverter.toSysDictItemOptionVo(sysDictItems);
        return success(sysDictItemOptionVo);
    }

    /**
     * 新增字典项
     *
     * @param request 请求参数
     * @return 新增结果
     */
    @PostMapping("/items")
    @Operation(summary = "新增字典项")
    @OperationLog(title = "字典项管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermission('system:dict-item:add')")
    public AjaxResult addDictItem(@Parameter(description = "字典项添加请求类")
                                  @Validated @RequestBody SysDictItemAddRequest request) {
        boolean result = sysDictItemService.addDictItem(request);
        return toAjax(result);
    }

    /**
     * 获取字典项详情
     *
     * @param id 字典项ID
     * @return 字典项详情
     */
    @GetMapping("/item/{id}")
    @Operation(summary = "获取字典项")
    @PreAuthorize("@ss.hasPermission('system:dict-item:query')")
    public AjaxResult getDictItemById(@Parameter(description = "字典项ID") @PathVariable("id") Long id) {
        if (id == null || id <= 0) return error("字典项ID不能为空");
        SysDictItem sysDictItem = sysDictItemService.getDictItemById(id);
        SysDictItemVo sysDictItemVo = sysDictConverter.toSysDictItemVo(sysDictItem);
        return success(sysDictItemVo);
    }

    /**
     * 修改字典项
     *
     * @param request 请求参数
     * @return 修改结果
     */
    @PutMapping("/items")
    @Operation(summary = "修改字典项")
    @OperationLog(title = "字典项管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:dict-item:update')")
    public AjaxResult updateDictItem(@Parameter(description = "字典项更新请求类")
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
    @DeleteMapping("/items/{ids}")
    @Operation(summary = "删除字典项")
    @OperationLog(title = "字典项管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:dict-item:remove')")
    public AjaxResult deleteDictItem(@Parameter(description = "删除字典项，支持批量删除，删除时如果一项删除失败数据将会回滚")
                                         @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> checkParam(id == null || id <= 0, "字典项ID不能为空!"));
        boolean result = sysDictItemService.deleteDictItem(ids);
        return toAjax(result);
    }

}
