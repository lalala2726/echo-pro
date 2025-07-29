package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.excel.utils.ExcelExportService;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.message.model.entity.SysMessage;
import cn.zhangchuangla.system.message.model.request.SysMessageAddRequest;
import cn.zhangchuangla.system.message.model.request.SysMessageQueryRequest;
import cn.zhangchuangla.system.message.model.request.SysMessageUpdateRequest;
import cn.zhangchuangla.system.message.model.request.SysSendMessageRequest;
import cn.zhangchuangla.system.message.model.vo.system.SysMessageListVo;
import cn.zhangchuangla.system.message.model.vo.system.SysMessageVo;
import cn.zhangchuangla.system.message.service.SysMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统消息表控制器
 *
 * @author Chuang
 * created on  2025-05-24
 */
@RestController
@RequestMapping("/system/message")
@RequiredArgsConstructor
@Tag(name = "站内信管理", description = "提供系统消息的列表、发送、用户消息操作、标记已读未读、详情、导出等相关接口")
public class MessageController extends BaseController {

    private final SysMessageService sysMessageService;
    private final ExcelExportService excelExportService;

    /**
     * 查询系统消息表列表
     */
    @Operation(summary = "查询系统消息表列表")
    @PreAuthorize("@ss.hasPermission('system.message:list')")
    @GetMapping("/list")
    public AjaxResult<TableDataResult> listSysMessage(@Parameter(description = "系统消息表列表查询参数")
                                                          @Validated @ParameterObject SysMessageQueryRequest request) {
        Page<SysMessage> page = sysMessageService.listSysMessage(request);
        List<SysMessageListVo> voList = copyListProperties(page, SysMessageListVo.class);
        return getTableData(page, voList);
    }

    /**
     * 管理员发送消息
     *
     * @return 操作结果
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息")
    @OperationLog(title = "消息管理", businessType = BusinessType.SEND_MESSAGES)
    @PreAuthorize("@ss.hasPermission('system:message:send')")
    public AjaxResult<Void> sendMessage(@RequestBody @Validated SysSendMessageRequest request) {
        boolean result = sysMessageService.sysSendMessage(request);
        return toAjax(result);
    }

    /**
     * 导出系统消息表列表
     */
    @Operation(summary = "导出系统消息表列表")
    @PreAuthorize("@ss.hasPermission('system.message:export')")
    @GetMapping("/export")
    @OperationLog(title = "系统消息表管理", businessType = BusinessType.EXPORT)
    public void export(HttpServletResponse response) {
        List<SysMessage> list = sysMessageService.list();
        List<SysMessageListVo> voList = copyListProperties(list, SysMessageListVo.class);
        excelExportService.exportExcel(response, voList, SysMessageListVo.class, "系统消息表列表");
    }

    /**
     * 获取系统消息表详细信息
     */
    @Operation(summary = "获取系统消息表详细信息")
    @PreAuthorize("@ss.hasPermission('system.message:query')")
    @GetMapping("/{id:\\d+}")
    public AjaxResult<SysMessageVo> getSysMessageById(@Parameter(description = "系统消息表ID")
                                                          @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "系统消息表ID必须大于0！");
        SysMessageVo sysMessage = sysMessageService.getSysMessageById(id);
        return success(sysMessage);
    }

    /**
     * 新增系统消息表
     */
    @Operation(summary = "新增系统消息表")
    @PreAuthorize("@ss.hasPermission('system.message:add')")
    @PostMapping
    @OperationLog(title = "系统消息表管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addSysMessage(@Parameter(description = "新增系统消息表请求参数")
                                              @Validated @RequestBody SysMessageAddRequest request) {
        boolean result = sysMessageService.addSysMessage(request);
        return toAjax(result);
    }

    /**
     * 修改系统消息表
     */
    @Operation(summary = "修改系统消息表")
    @PreAuthorize("@ss.hasPermission('system.message:edit')")
    @PutMapping
    @OperationLog(title = "系统消息表管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateSysMessage(@Parameter(description = "修改系统消息表请求参数")
                                                 @Validated @RequestBody SysMessageUpdateRequest request) {
        boolean result = sysMessageService.updateSysMessage(request);
        return toAjax(result);
    }

    /**
     * 删除系统消息表
     */
    @Operation(summary = "删除系统消息表")
    @PreAuthorize("@ss.hasPermission('system.message:remove')")
    @DeleteMapping("/{ids:[\\d,]+}")
    @OperationLog(title = "系统消息表管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteSysMessageByIds(@Parameter(description = "系统消息表ID集合，支持批量删除")
                                                  @PathVariable("ids") List<Long> ids) {
        boolean result = sysMessageService.deleteSysMessageByIds(ids);
        return toAjax(result);
    }
}
