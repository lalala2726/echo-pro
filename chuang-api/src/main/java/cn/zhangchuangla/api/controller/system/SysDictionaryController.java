package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.constant.SystemMessageConstant;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryRequest;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryListVo;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryVo;
import cn.zhangchuangla.system.service.DictionaryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/2 23:07
 */
@Tag(name = "字典管理")
@RestController
@Anonymous
@RequestMapping("/system/dictionary")
public class SysDictionaryController extends BaseController {


    private final DictionaryService dictionaryService;

    @Autowired
    public SysDictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }


    /**
     * 字典列表
     *
     * @return 返回字典列表
     */
    @Operation(summary = "字典列表")
    @GetMapping("/list")
    @PreAuthorize("@auth.hasPermission('system:dictionary:list')")
    public TableDataResult list(@Validated DictionaryRequest request) {
        Page<Dictionary> list = dictionaryService.getDictionaryList(request);
        List<DictionaryListVo> dictionaryListVos = copyListProperties(list, DictionaryListVo.class);
        return getTableData(list, dictionaryListVos);
    }

    /**
     * 新增字典
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Operation(summary = "新增字典")
    @PostMapping
    @PreAuthorize("@auth.hasPermission('system:dictionary:add')")
    @Log(title = "字典管理", businessType = BusinessType.INSERT)
    public AjaxResult addDictionary(@Validated @RequestBody AddDictionaryRequest request) {
        checkParam(dictionaryService.isNameExist(request.getName()), "字典名称已存在!");
        dictionaryService.addDictionary(request);
        return success(SystemMessageConstant.ADD_SUCCESS);
    }

    /**
     * 根据ID获取字典详情
     *
     * @param id 字典ID
     * @return 字典详情
     */
    @Operation(summary = "字典详情")
    @GetMapping("/{id}")
    @PreAuthorize("@auth.hasPermission('system:dictionary:info')")
    public AjaxResult getDictionaryById(@PathVariable("id") Long id) {
        checkParam(id == null || id > 0, "字典ID不能小于等于零!");
        Dictionary dictionary = dictionaryService.getDictionaryById(id);
        DictionaryVo dictionaryVo = new DictionaryVo();
        BeanUtils.copyProperties(dictionary, dictionaryVo);
        return success(dictionaryVo);
    }

    /**
     * 根据ID修改字典信息
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Operation(summary = "修改字典")
    @PutMapping
    @PreAuthorize("@auth.hasPermission('system:dictionary:update')")
    @Log(title = "字典管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateDictionary(@Validated @RequestBody UpdateDictionaryRequest request) {
        checkParam(dictionaryService.isNameExistExceptCurrent(request.getId(), request.getName()), "字典名称已存在!");
        return toAjax(dictionaryService.updateDictionaryById(request));
    }

    /**
     * 根据ID删除字典,支持批量删除,当字典下面有字典项时,删除失败并回滚数据
     *
     * @param ids 字典ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@auth.hasPermission('system:dictionary:delete')")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteDictionary(@PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id > 0, "字典ID不能小于等于零!");
        });
        dictionaryService.deleteDictionary(ids);
        return success();
    }


}
