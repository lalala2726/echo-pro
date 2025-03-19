package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.system.model.entity.DictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryDataRequest;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryDataBasicVo;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryDataListVo;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryDataVo;
import cn.zhangchuangla.system.service.DictionaryDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/2 23:07
 */
@Tag(name = "字典值")
@RestController
@Anonymous
@RequestMapping("/system/dictionary/data")
public class DictionaryDataController {

    private final DictionaryDataService dictionaryDataService;

    public DictionaryDataController(DictionaryDataService dictionaryDataService) {
        this.dictionaryDataService = dictionaryDataService;
    }


    /**
     * 根据字典ID获取字典值
     *
     * @param id      字典ID
     * @param request 请求参数
     * @return 返回字典值分页参数
     */
    @GetMapping("/dictId/{id}")
    @Operation(summary = "根据字典名称获取字典值")
    @PreAuthorize("@auth.hasPermission('system:dictionary-data:list')")
    public AjaxResult getDictDataByDictionaryName(@PathVariable("id") Long id, @Validated DictionaryDataRequest request) {
        ParamsUtils.minValidParam(id, "字典ID不能小于等于零!");
        ArrayList<DictionaryDataListVo> dictionaryDataListVos = new ArrayList<>();
        Page<DictionaryData> dictionaryDataPage = dictionaryDataService.getDictDataByDictionaryName(id, request);
        dictionaryDataPage.getRecords().forEach(item -> {
            DictionaryDataListVo dictionaryDataListVo = new DictionaryDataListVo();
            BeanUtils.copyProperties(item, dictionaryDataListVo);
            dictionaryDataListVos.add(dictionaryDataListVo);
        });
        return AjaxResult.table(dictionaryDataPage, dictionaryDataListVos);
    }

    /**
     * 根据字典名称获取字典值
     *
     * @param dictionaryName 字典名称
     * @return 字典值列表
     */
    @Operation(summary = "根据字典名称获取字典值")
    @GetMapping("/dictName/{dictionaryName}")
    @PreAuthorize("@auth.hasPermission('system:dictionary-data:list')")
    public AjaxResult getDictionaryDataByDictionaryName(@PathVariable("dictionaryName") String dictionaryName) {
        ParamsUtils.paramsNotIsNullOrBlank("字典名称不能为空", dictionaryName);
        List<DictionaryData> result = dictionaryDataService.getDictionaryDataByIdDictName(dictionaryName);
        ArrayList<DictionaryDataBasicVo> dictionaryDataBasicVos = new ArrayList<>();
        result.forEach(item -> {
            DictionaryDataBasicVo dictionaryDataBasicVo = new DictionaryDataBasicVo();
            BeanUtils.copyProperties(item, dictionaryDataBasicVo);
            dictionaryDataBasicVos.add(dictionaryDataBasicVo);
        });
        return AjaxResult.success(dictionaryDataBasicVos);
    }

    /**
     * 添加字典值
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Operation(summary = "添加字典值")
    @PostMapping
    @PreAuthorize("@auth.hasPermission('system:dictionary-data:add')")
    public AjaxResult addDictionaryData(@Validated @RequestBody AddDictionaryDataRequest request) {
        ParamsUtils.objectIsNull(request, "参数不能为空!");
        ParamsUtils.minValidParam(request.getDictionaryId(), "字典ID不能小于等于零!");
        boolean isExits = dictionaryDataService.noDuplicateKeys(request.getDataKey());
        ParamsUtils.paramCheck(isExits, "字典项键已存在!");
        boolean result = dictionaryDataService.addDictionaryData(request);
        return AjaxResult.isSuccess(result);
    }

    /**
     * 根据ID获取字典项详情
     *
     * @param id 字典值ID
     * @return 字典值详情
     */
    @Operation(summary = "字典值详情")
    @GetMapping("/{id}")
    @PreAuthorize("@auth.hasPermission('system:dictionary-data:info')")
    public AjaxResult getDictionaryItemById(@PathVariable("id") Long id) {
        ParamsUtils.minValidParam(id, "字典值ID不能小于等于零!");
        DictionaryData dictionaryData = dictionaryDataService.getDictionaryById(id);
        DictionaryDataVo dictionaryDataVo = new DictionaryDataVo();
        BeanUtils.copyProperties(dictionaryData, dictionaryDataVo);
        return AjaxResult.success(dictionaryDataVo);
    }

    /**
     * 修改字典值
     *
     * @param request 请求参数
     * @return 返回结果
     */
    @Operation(summary = "修改字典值")
    @PutMapping
    @PreAuthorize("@auth.hasPermission('system:dictionary-data:update')")
    public AjaxResult updateDictionaryData(@Validated @RequestBody UpdateDictionaryDataRequest request) {
        ParamsUtils.objectIsNull(request, "参数不能为空!");
        boolean isExist = dictionaryDataService.noDuplicateKeys(request.getDataKey());
        ParamsUtils.paramCheck(isExist, "字典项键已存在!");
        boolean result = dictionaryDataService.updateDictionaryData(request);
        return AjaxResult.success(result);
    }

    /**
     * 删除字典值,支持批量删除
     *
     * @param ids 字典值ID
     * @return 返回结果
     */
    @Operation(summary = "删除字典值")
    @DeleteMapping("/{id}")
    @PreAuthorize("@auth.hasPermission('system:dictionary-data:delete')")
    public AjaxResult deleteDictionaryData(@PathVariable("id") List<Long> ids) {
        ParamsUtils.objectIsNull(ids, "字典值ID不能为空!");
        ids.forEach(id -> {
            ParamsUtils.minValidParam(id, "字典值ID不能小于等于零!");
        });
        dictionaryDataService.deleteDictionaryData(ids);
        return AjaxResult.success();
    }


}
