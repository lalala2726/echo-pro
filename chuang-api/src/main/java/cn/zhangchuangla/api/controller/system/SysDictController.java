package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.StrUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysDictKey;
import cn.zhangchuangla.system.model.entity.SysDictValue;
import cn.zhangchuangla.system.model.request.dict.*;
import cn.zhangchuangla.system.model.vo.dict.SysDictKeyVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictValueListVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictValueVo;
import cn.zhangchuangla.system.service.SysDictKeyService;
import cn.zhangchuangla.system.service.SysDictValueService;
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

    private final SysDictKeyService sysDictKeyService;
    private final SysDictValueService sysDictValueService;

    /**
     * 获取字典类型列表
     *
     * @param request 字典类型列表查询参数
     * @return 字典类型列表
     */
    @GetMapping("/key/list")
    @Operation(summary = "字典类型列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public AjaxResult<TableDataResult> listDictKey(@Parameter(description = "字典类型列表查询参数")
                                                   @Validated @ParameterObject SysDictKeyQueryRequest request) {
        Page<SysDictKey> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysDictKey> sysDictKeyPage = sysDictKeyService.listDictKey(page, request);
        List<SysDictKeyVo> sysDictKeyVos = copyListProperties(sysDictKeyPage, SysDictKeyVo.class);
        return getTableData(sysDictKeyPage, sysDictKeyVos);
    }

    /**
     * 获取字典类型详情
     *
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    @GetMapping("/key/{id:\\d+}")
    @Operation(summary = "字典类型详情")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public AjaxResult<SysDictKeyVo> getDictKey(@Parameter(description = "字典类型ID") @PathVariable("id") Long id) {
        Assert.notNull(id, "部门ID不能为空！");
        Assert.isTrue(id > 0, "部门ID必须大于0！");

        SysDictKey sysDictKey = sysDictKeyService.getDictKeyById(id);
        SysDictKeyVo sysDictKeyVo = new SysDictKeyVo();
        if (sysDictKey != null) {
            BeanUtils.copyProperties(sysDictKey, sysDictKeyVo);
        }
        return success(sysDictKeyVo);
    }

    /**
     * 添加字典类型
     *
     * @param request 字典类型添加请求参数
     * @return 添加结果
     */
    @PostMapping("/key")
    @Operation(summary = "添加字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @OperationLog(title = "字典类型", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addDictKey(@Parameter(description = "字典类型添加请求参数")
                                       @Validated @RequestBody SysDictKeyAddRequest request) {
        boolean result = sysDictKeyService.addDictKey(request);
        return toAjax(result);
    }

    /**
     * 修改字典类型
     *
     * @param request 字典类型修改请求参数
     * @return 修改结果
     */
    @PutMapping("/key")
    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @OperationLog(title = "字典类型", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateDictKey(@Parameter(description = "字典类型修改请求参数")
                                          @Validated @RequestBody SysDictKeyUpdateRequest request) {
        boolean result = sysDictKeyService.updateDictKey(request);
        return toAjax(result);
    }

    /**
     * 删除字典类型
     *
     * @param ids 字典类型ID列表
     * @return 删除结果
     */
    @DeleteMapping("/key/{ids:[\\d,]+}")
    @Operation(summary = "删除字典键")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperationLog(title = "字典类型", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteDictKey(@Parameter(description = "字典键ID列表") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "字典键ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "字典键ID必须大于0！");
        boolean result = sysDictKeyService.deleteDictKey(ids);
        return toAjax(result);
    }

    /**
     * 获取所有字典类型
     *
     * @return 所有字典类型
     */
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    @Operation(summary = "获取所有字典类型")
    @GetMapping("/key/options")
    public AjaxResult<List<Option<String>>> getAllDictKey() {
        List<Option<String>> options = sysDictKeyService.getAllDictKey();
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
        boolean result = sysDictKeyService.refreshCache();
        return toAjax(result);
    }

    /**
     * 获取字典项列表
     *
     * @param request 字典项列表查询参数
     * @return 字典项列表
     */
    @GetMapping("/value/{dictKey}/list")
    @Operation(summary = "字典项列表")
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public AjaxResult<TableDataResult> listDictValue(@PathVariable("dictKey") String dictKey,
                                                     @Parameter(description = "字典项列表查询参数")
                                                     @Validated @ParameterObject SysDictValueQueryRequest request) {
        if (StrUtils.isBlank(dictKey)) {
            return error("字典值不能为空!");
        }
        Page<SysDictValue> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysDictValue> sysDictValuePage = sysDictValueService.listDictValue(page, dictKey, request);
        List<SysDictValueListVo> dictValueListVos = copyListProperties(sysDictValuePage, SysDictValueListVo.class);
        return getTableData(sysDictValuePage, dictValueListVos);
    }

    /**
     * 获取字典项选项
     *
     * @param dictKey 字典类型编码
     * @return 字典项选项
     */
    @GetMapping("/value/option/{dictKey}")
    @Operation(summary = "字典项选项")
    public AjaxResult<List<Option<String>>> getDictValueOption(@PathVariable("dictKey") String dictKey) {
        if (StrUtils.isBlank(dictKey)) {
            return error("字典类型编码不能为空!");
        }
        List<Option<String>> options = sysDictValueService.getDictValueOption(dictKey);
        return success(options);

    }

    /**
     * 获取字典项详情
     *
     * @param id 字典项ID
     * @return 字典项详情
     */
    @GetMapping("/value/{id:\\d+}")
    @Operation(summary = "字典项详情")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public AjaxResult<SysDictValueVo> getDictValue(@Parameter(description = "字典项ID") @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "字典项ID必须大于0！");
        SysDictValue sysDictValue = sysDictValueService.getDictValueById(id);
        SysDictValueVo sysDictValueVo = new SysDictValueVo();
        if (sysDictValue != null) {
            BeanUtils.copyProperties(sysDictValue, sysDictValueVo);
        }
        return success(sysDictValueVo);
    }

    /**
     * 添加字典项
     *
     * @param request 字典项添加请求参数
     * @return 添加结果
     */
    @PostMapping("/value")
    @Operation(summary = "添加字典项")
    @PreAuthorize("@ss.hasPermission('system:dict:add')")
    @OperationLog(title = "字典项", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addDictValue(@Parameter(description = "字典项添加请求参数")
                                         @Validated @RequestBody SysDictValueAddRequest request) {
        // 检查字典类型是否存在
        if (!sysDictKeyService.isDictKeyExist(request.getDictKey())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典类型不存在: " + request.getDictKey());
        }
        boolean result = sysDictValueService.addDictValue(request);
        return toAjax(result);
    }

    /**
     * 修改字典项
     *
     * @param request 字典项修改请求参数
     * @return 修改结果
     */
    @PutMapping("/value")
    @Operation(summary = "修改字典项")
    @PreAuthorize("@ss.hasPermission('system:dict:edit')")
    @OperationLog(title = "字典项", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateDictValue(@Parameter(description = "字典项修改请求参数")
                                            @Validated @RequestBody SysDictValueUpdateRequest request) {
        boolean result = sysDictValueService.updateDictValue(request);
        return toAjax(result);
    }

    /**
     * 删除字典项
     *
     * @param ids 字典项ID列表
     * @return 删除结果
     */
    @DeleteMapping("/value/{ids:[\\d,]+}")
    @Operation(summary = "删除字典项")
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    @OperationLog(title = "字典项", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteDictValue(@Parameter(description = "字典项ID列表") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "字典值ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "字典值ID必须大于0！");

        boolean result = sysDictValueService.deleteDictValue(ids);
        return toAjax(result);
    }
}
