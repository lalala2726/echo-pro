package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
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
@Tag(name = "字典值")
@RestController
@Anonymous
@RequestMapping("/system/dictionary/data")
public class SysDictionaryDataController extends BaseController {

    private final DictionaryDataService dictionaryDataService;

    @Autowired
    public SysDictionaryDataController(DictionaryDataService dictionaryDataService) {
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
    public TableDataResult getDictDataByDictionaryName(@PathVariable("id") Long id, @Validated DictionaryDataRequest request) {
        checkParam(id == null || id > 0, "字典ID不能小于等于零!");
        Page<DictionaryData> dictionaryDataPage = dictionaryDataService.getDictDataByDictionaryName(id, request);
        List<DictionaryDataListVo> dictionaryDataListVos = copyListProperties(dictionaryDataPage, DictionaryDataListVo.class);
        return getTableData(dictionaryDataPage, dictionaryDataListVos);
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
        List<DictionaryData> result = dictionaryDataService.getDictionaryDataByIdDictName(dictionaryName);
        List<DictionaryDataBasicVo> dictionaryDataBasicVos = copyListProperties(result, DictionaryDataBasicVo.class);
        return success(dictionaryDataBasicVos);
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
    @OperationLog(title = "字典值管理", businessType = BusinessType.INSERT)
    public AjaxResult addDictionaryData(@Validated @RequestBody AddDictionaryDataRequest request) {
        return toAjax(dictionaryDataService.addDictionaryData(request));
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
        checkParam(id == null || id > 0, "字典值ID不能小于等于零!");
        DictionaryData dictionaryData = dictionaryDataService.getDictionaryById(id);
        DictionaryDataVo dictionaryDataVo = new DictionaryDataVo();
        BeanUtils.copyProperties(dictionaryData, dictionaryDataVo);
        return success(dictionaryDataVo);
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
    @OperationLog(title = "字典值管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateDictionaryData(@Validated @RequestBody UpdateDictionaryDataRequest request) {
        checkParam(request == null, "参数不能为空!");
        return success(dictionaryDataService.updateDictionaryData(request));
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
    @OperationLog(title = "字典值管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteDictionaryData(@PathVariable("id") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "字典值ID不能为空!");
        });
        dictionaryDataService.deleteDictionaryData(ids);
        return success();
    }


}
