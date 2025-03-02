package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/2 23:07
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/system/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Operation(summary = "字典列表")
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success();
    }

    @Operation(summary = "新增字典")
    @PostMapping
    public AjaxResult addDictionary(@RequestBody AddDictionaryRequest request) {
        ParamsUtils.paramsNotIsNullOrBlank("字典名称不能为空!", request.getName());
        ParamsUtils.isParamValid(dictionaryService.isNameExist(request.getName()), "字典名称已存在!");
        dictionaryService.addDictionary(request);
        return AjaxResult.success();
    }

    @Operation(summary = "字典详情")
    @GetMapping("/{id}")
    public AjaxResult getDictionaryById(@PathVariable("id") Long id) {
        return AjaxResult.success();
    }

    @Operation(summary = "修改字典")
    @PutMapping
    public AjaxResult updateDictionary() {
        return AjaxResult.success();
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{id}")
    public AjaxResult deleteDictionary(@PathVariable("id") List<Long> ids) {
        return AjaxResult.success();
    }


}
