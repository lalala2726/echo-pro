package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.converter.SysDictConverter;
import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.request.dict.SysDictAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictListRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictUpdateRequest;
import cn.zhangchuangla.system.model.vo.dict.SysDictListVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictVo;
import cn.zhangchuangla.system.service.SysDictItemService;
import cn.zhangchuangla.system.service.SysDictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 13:33
 */
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
    public AjaxResult listDict(SysDictListRequest request) {
        Page<SysDict> sysDict = sysDictService.listDict(request);
        List<SysDictListVo> sysDictListVos = copyListProperties(sysDict, SysDictListVo.class);
        return success(getTableData(sysDict, sysDictListVos));
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
    public AjaxResult addDict(@Validated @RequestBody SysDictAddRequest request) {
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
    public AjaxResult getDict(@PathVariable("id") Long id) {
        SysDict sysDict = sysDictService.getDictById(id);
        SysDictVo sysDictVo = sysDictConverter.toSysDictVo(sysDict);
        return success(sysDictVo);
    }

    /**
     * 删除字典
     *
     * @param ids 字典ID列表
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典")
    @OperationLog(title = "字典管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:dict:remove')")
    public AjaxResult deleteDict(@PathVariable List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "字典ID不能为空!");
        });
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
    public AjaxResult updateDict(@Validated @RequestBody SysDictUpdateRequest request) {
        boolean result = sysDictService.updateDict(request);
        return toAjax(result);
    }

}
