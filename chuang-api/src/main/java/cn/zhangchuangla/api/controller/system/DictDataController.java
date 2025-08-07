package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysDictData;
import cn.zhangchuangla.system.core.model.request.dict.SysDictDataAddRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictDataQueryRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictDataUpdateRequest;
import cn.zhangchuangla.system.core.model.vo.dict.SysDictDataVo;
import cn.zhangchuangla.system.core.service.SysDictDataService;
import cn.zhangchuangla.system.core.service.SysDictTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 *
 * @author Chuang
 */
@Slf4j
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
@RestController
@Tag(name = "字典数据管理", description = "提供字典数据的增删改查等相关接口")
public class DictDataController extends BaseController {

    private final SysDictDataService sysDictDataService;
    private final SysDictTypeService sysDictTypeService;

    /**
     * 获取字典数据列表
     *
     * @param dictType 字典类型
     * @param request  字典数据列表查询参数
     * @return 字典数据列表
     */
    @GetMapping("/{dictType}/list")
    @Operation(summary = "字典数据列表")
    @PreAuthorize("@ss.hasPermission('system:dict-data:list')")
    public AjaxResult<TableDataResult> listDictData(@PathVariable("dictType") String dictType,
                                                    @Parameter(description = "字典数据列表查询参数")
                                                    @Validated @ParameterObject SysDictDataQueryRequest request) {
        if (StringUtils.isBlank(dictType)) {
            return error("字典类型不能为空!");
        }
        Page<SysDictData> sysDictDataPage = sysDictDataService.listDictData(dictType, request);
        List<SysDictDataVo> dictDataVos = copyListProperties(sysDictDataPage, SysDictDataVo.class);
        return getTableData(sysDictDataPage, dictDataVos);
    }

    /**
     * 获取字典数据选项
     *
     * @param dictType 字典类型
     * @return 字典数据选项
     */
    @GetMapping("/option/{dictType}")
    @Operation(summary = "字典数据选项")
    public AjaxResult<List<Option<String>>> getDictDataOption(@PathVariable("dictType") String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return error("字典类型不能为空!");
        }
        List<Option<String>> options = sysDictDataService.getDictDataOption(dictType);
        return success(options);
    }

    /**
     * 获取字典数据详情
     *
     * @param id 字典数据ID
     * @return 字典数据详情
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "字典数据详情")
    @PreAuthorize("@ss.hasPermission('system:dict-data:query')")
    public AjaxResult<SysDictDataVo> getDictData(@Parameter(description = "字典数据ID") @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "字典数据ID必须大于0！");
        SysDictData sysDictData = sysDictDataService.getDictDataById(id);
        SysDictDataVo sysDictDataVo = new SysDictDataVo();
        if (sysDictData != null) {
            BeanUtils.copyProperties(sysDictData, sysDictDataVo);
        }
        return success(sysDictDataVo);
    }

    /**
     * 添加字典数据
     *
     * @param request 字典数据添加请求参数
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict-data:add')")
    @OperationLog(title = "字典数据", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addDictData(@Parameter(description = "字典数据添加请求参数")
                                        @Validated @RequestBody SysDictDataAddRequest request) {
        // 检查字典类型是否存在
        if (!sysDictTypeService.isDictTypeExist(request.getDictType())) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "字典类型不存在: " + request.getDictType());
        }
        boolean result = sysDictDataService.addDictData(request);
        return toAjax(result);
    }

    /**
     * 修改字典数据
     *
     * @param request 字典数据修改请求参数
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict-data:update')")
    @OperationLog(title = "字典数据", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateDictData(@Parameter(description = "字典数据修改请求参数")
                                           @Validated @RequestBody SysDictDataUpdateRequest request) {
        boolean result = sysDictDataService.updateDictData(request);
        return toAjax(result);
    }

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID列表
     * @return 删除结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict-data:delete')")
    @OperationLog(title = "字典数据", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteDictData(@Parameter(description = "字典数据ID列表") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "字典数据ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "字典数据ID必须大于0！");

        boolean result = sysDictDataService.deleteDictData(ids);
        return toAjax(result);
    }
}
