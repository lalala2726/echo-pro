package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.system.model.entity.DictionaryItem;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryItemRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryItemRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryItemRequest;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryItemBasicVo;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryItemListVo;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryItemVo;
import cn.zhangchuangla.system.service.DictionaryItemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("/system/dictionary/item")
public class DictionaryItemController {

    private final DictionaryItemService dictionaryItemService;

    public DictionaryItemController(DictionaryItemService dictionaryItemService) {
        this.dictionaryItemService = dictionaryItemService;
    }

    @Operation(summary = "字典值列表")
    @GetMapping("/list")
    public AjaxResult list(DictionaryItemRequest request) {
        Page<DictionaryItem> page = dictionaryItemService.dictionaryItemList(request);
        ArrayList<DictionaryItemListVo> dictionaryItemListVos = new ArrayList<>();
        page.getRecords().forEach(item -> {
            DictionaryItemListVo dictionaryItemListVo = new DictionaryItemListVo();
            BeanUtils.copyProperties(item, dictionaryItemListVo);
            dictionaryItemListVos.add(dictionaryItemListVo);
        });
        return AjaxResult.table(page, dictionaryItemListVos);
    }

    @GetMapping("/type/{dictionaryName}")
    public AjaxResult getDictionaryItemByDictionaryName(@PathVariable("dictionaryName") String dictionaryName) {
        ParamsUtils.paramsNotIsNullOrBlank("字典名称不能为空", dictionaryName);
        List<DictionaryItem> result = dictionaryItemService.getDictionaryItemByIdDictName(dictionaryName);
        ArrayList<DictionaryItemBasicVo> dictionaryItemBasicVos = new ArrayList<>();
        result.forEach(item -> {
            DictionaryItemBasicVo dictionaryItemBasicVo = new DictionaryItemBasicVo();
            BeanUtils.copyProperties(item, dictionaryItemBasicVo);
            dictionaryItemBasicVos.add(dictionaryItemBasicVo);
        });
        return AjaxResult.success(dictionaryItemBasicVos);
    }

    @Operation(summary = "添加字典值")
    @PostMapping
    public AjaxResult addDictionaryItem(@RequestBody AddDictionaryItemRequest request) {
        ParamsUtils.objectIsNull(request, "参数不能为空!");
        ParamsUtils.minValidParam(request.getDictionaryId(), "字典ID不能小于等于零!");
        ParamsUtils.paramsNotIsNullOrBlank("字典项键不能为空", request.getItemKey());
        ParamsUtils.paramsNotIsNullOrBlank("字典项值不能为空", request.getItemValue());
        boolean isExits = dictionaryItemService.noDuplicateKeys(request.getItemKey());
        ParamsUtils.isParamValid(isExits, "字典项键已存在!");
        boolean result = dictionaryItemService.addDictionaryItem(request);
        return AjaxResult.isSuccess(result);
    }

    @Operation(summary = "字典值详情")
    @GetMapping("/{id}")
    public AjaxResult getDictionaryItemById(@PathVariable("id") Long id) {
        ParamsUtils.minValidParam(id, "字典值ID不能小于等于零!");
        DictionaryItem dictionaryItem = dictionaryItemService.getDictionaryById(id);
        DictionaryItemVo dictionaryItemVo = new DictionaryItemVo();
        BeanUtils.copyProperties(dictionaryItem, dictionaryItemVo);
        return AjaxResult.success(dictionaryItemVo);
    }

    @Operation(summary = "修改字典值")
    @PutMapping
    public AjaxResult updateDictionaryItem(UpdateDictionaryItemRequest request) {
        ParamsUtils.objectIsNull(request, "参数不能为空!");
        ParamsUtils.paramsNotIsNullOrBlank("字典项键不能为空", request.getItemKey());
        ParamsUtils.paramsNotIsNullOrBlank("字典项值不能为空", request.getItemValue());
        boolean isExist = dictionaryItemService.noDuplicateKeys(request.getItemKey());
        ParamsUtils.isParamValid(isExist, "字典项键已存在!");
        boolean result = dictionaryItemService.updateDictionaryItem(request);
        return AjaxResult.success(result);
    }

    @Operation(summary = "删除字典值")
    @DeleteMapping("/{id}")
    public AjaxResult deleteDictionaryItem(@PathVariable("id") List<Long> ids) {
        ParamsUtils.objectIsNull(ids, "字典值ID不能为空!");
        ids.forEach(id -> {
            ParamsUtils.minValidParam(id, "字典值ID不能小于等于零!");
        });
        dictionaryItemService.deleteDictionaryItem(ids);
        return AjaxResult.success();
    }


}
