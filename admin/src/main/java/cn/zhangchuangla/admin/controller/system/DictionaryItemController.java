package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.DictionaryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/2 23:07
 */
@Tag(name = "字典值")
@RestController
@RequestMapping("/system/dictionary/item")
public class DictionaryItemController {

    private final DictionaryItemService dictionaryItemService;

    public DictionaryItemController(DictionaryItemService dictionaryItemService) {
        this.dictionaryItemService = dictionaryItemService;
    }

    @Operation(summary = "字典值列表")
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success();
    }

    @Operation(summary = "添加字典值")
    @PostMapping
    public AjaxResult addDictionary() {
        return AjaxResult.success();
    }

    @Operation(summary = "字典值详情")
    @GetMapping("/{id}")
    public AjaxResult getDictionaryById(@PathVariable("id") Long id) {
        return AjaxResult.success();
    }

    @Operation(summary = "修改字典值")
    @PutMapping
    public AjaxResult updateDictionary() {
        return AjaxResult.success();
    }

    @Operation(summary = "删除字典值")
    @DeleteMapping("/{id}")
    public AjaxResult deleteDictionary(@PathVariable("id") List<Long> ids) {
        return AjaxResult.success();
    }


}
