package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import cn.zhangchuangla.framework.security.device.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 */
@Slf4j
@RestController
@RequestMapping("/system/device")
@Tag(name = "设备管理", description = "设备的相关管理")
@RequiredArgsConstructor
public class OnlineDeviceController extends BaseController {

    private final DeviceService deviceService;
    private final ExcelExporter excelExporter;


    /**
     * 查询当前系统中所有有效的设备列表
     *
     * @param request 查选参数
     * @return 设备列表
     */
    @GetMapping("/list")
    @Operation(summary = "设备列表")
    @PreAuthorize("@ss.hasPermission('system:online:device:list')")
    public AjaxResult<TableDataResult> listDevice(SessionDeviceQueryRequest request) {
        PageResult<SessionDevice> sessionDevicePageResult = deviceService.listDevice(request);
        return getTableData(sessionDevicePageResult);
    }


    /**
     * 删除会话
     *
     * @param refreshTokenId 刷新令牌ID
     * @return 是否成功删除会话
     */
    @Operation(summary = "删除设备", description = "删除设备会删除关联的会话")
    @DeleteMapping
    @OperationLog(title = "会话管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:session:delete')")
    public AjaxResult<Void> deleteSession(@RequestParam("refreshTokenId") String refreshTokenId) {
        boolean result = deviceService.deleteDevice(refreshTokenId);
        return result ? success() : error();
    }


    /**
     * 导出在线设备列表
     *
     * @param request  请求参数
     * @param response 响应
     */
    @PostMapping("/export")
    @OperationLog(title = "会话管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:session:export')")
    public void exportSession(@RequestBody SessionDeviceQueryRequest request, HttpServletResponse response) {
        request.setPageNum(-1);
        request.setPageNum(-1);
        PageResult<SessionDevice> sessionDevicePageResult = deviceService.listDevice(request);
        List<SessionDevice> rows = sessionDevicePageResult.getRows();
        excelExporter.exportExcel(response, rows, SessionDevice.class, "设备列表");
    }


}
