package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.framework.model.vo.OnlineLoginUser;
import cn.zhangchuangla.framework.security.session.SessionService;
import cn.zhangchuangla.system.core.model.request.monitor.OnlineUserQueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/28 14:41
 */
@RestController
@RequestMapping("/system/session")
@Slf4j
@RequiredArgsConstructor
public class OnlineSessionController extends BaseController {

    private final SessionService sessionService;
    private final ExcelExporter excelExporter;

    /**
     * 会话列表
     *
     * @param request 查询参数
     * @return 会话列表
     */
    @GetMapping("/list")
    @Operation(summary = "会话列表")
    @PreAuthorize("@ss.hasPermission('system:online:session:list')")
    public AjaxResult<TableDataResult> sessionList(OnlineUserQueryRequest request) {
        PageResult<OnlineLoginUser> onlineUserPageResult = sessionService.sessionList(request);
        return getTableData(onlineUserPageResult);
    }

    /**
     * 获取会话详情
     *
     * @param accessTokenId 访问令牌ID
     * @return 会话详情
     */
    @GetMapping("/detail")
    @Operation(summary = "会话详情")
    @PreAuthorize("@ss.hasPermission('system:online:session:query')")
    public AjaxResult<OnlineLoginUser> sessionDetail(@RequestParam("accessTokenId") String accessTokenId) {
        OnlineLoginUser onlineLoginUser = sessionService.sessionDetail(accessTokenId);
        return AjaxResult.success(onlineLoginUser);
    }

    /**
     * 删除会话
     *
     * @param accessTokenId 访问令牌会话ID
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除会话")
    @OperationLog(title = "会话管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:online:session:delete')")
    public AjaxResult<Void> deleteSession(@RequestParam("accessTokenId") String accessTokenId) {
        boolean result = sessionService.deleteSession(accessTokenId);
        return toAjax(result);
    }

    /**
     * 导出会话列表
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    @PostMapping("/export")
    @Operation(summary = "导出会话数据")
    @OperationLog(title = "会话管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('monitor:session:export')")
    public void exportSession(@RequestBody OnlineUserQueryRequest request, HttpServletResponse response) {
        request.setPageNum(-1);
        request.setPageSize(-1);
        PageResult<OnlineLoginUser> onlineLoginUserPageResult = sessionService.sessionList(request);
        excelExporter.exportExcel(response, onlineLoginUserPageResult.getRows(), OnlineLoginUser.class, "登录用户列表");
    }

}
