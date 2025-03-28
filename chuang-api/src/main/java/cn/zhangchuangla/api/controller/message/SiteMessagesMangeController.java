package cn.zhangchuangla.api.controller.message;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.base.BasePageRequest;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.message.model.entity.SiteMessages;
import cn.zhangchuangla.message.model.request.SiteMessageRequest;
import cn.zhangchuangla.message.model.vo.SiteMessagesManageList;
import cn.zhangchuangla.message.service.SiteMessagesService;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * 站内信管理
 * created on 2025/3/26 22:10
 */
@RestController
@RequestMapping("/message/site/manage")
@Tag(name = "站内信管理")
public class SiteMessagesMangeController extends BaseController {

    private final SiteMessagesService siteMessagesService;
    private final SysUserService sysUserService;

    public SiteMessagesMangeController(SiteMessagesService siteMessagesService, SysUserService sysUserService) {
        this.siteMessagesService = siteMessagesService;
        this.sysUserService = sysUserService;
    }


    /**
     * 获取站内信列表
     *
     * @param request 分页请求
     * @return 站内信列表
     */
    @GetMapping("/list")
    @PreAuthorize("@auth.hasPermission('message:manage:list')")
    @Operation(summary = "获取站内信列表")
    public TableDataResult listSiteMessages(BasePageRequest request) {
        Page<SiteMessages> messagesPage = siteMessagesService.listSiteMessages(request);
        List<SiteMessagesManageList> siteMessagesManageLists = copyListProperties(messagesPage, SiteMessagesManageList.class);
        return getTableData(messagesPage, siteMessagesManageLists);
    }


    /**
     * 给指定用户发送站内信
     *
     * @param siteMessageRequest 站内信信息
     * @return 操作结果
     */
    @PostMapping("/send")
    @Operation(summary = "给指定用户发送站内信")
    @PreAuthorize("@auth.hasPermission('message:site:send')")
    @Log(title = "站内信管理", businessType = BusinessType.SEND_MESSAGES)
    public AjaxResult sendSiteMessage(@RequestBody @Validated SiteMessageRequest siteMessageRequest) {
        boolean result = siteMessagesService.sendSiteMessage(siteMessageRequest);
        return toAjax(result);
    }


    /**
     * 给所有用户发送站内消息
     *
     * @param siteMessageRequest 站内信信息
     * @return 操作结果
     */
    @PostMapping("/send/all")
    @Operation(summary = "给所有用户发送站内消息")
    @PreAuthorize("@auth.hasPermission('message:site:send')")
    @Log(title = "站内信管理", businessType = BusinessType.SEND_MESSAGES)
    public AjaxResult sendSiteMessageToAllUsers(@RequestBody @Validated SiteMessageRequest siteMessageRequest) {
        //fixme 改进给所有用户发送消息，重新设计架构，当存在发送的用户过多时候，使用消息队列重构设计
        List<Long> userIdList = sysUserService.list().stream()
                .map(SysUser::getUserId)
                .toList();
        siteMessageRequest.setUserId(userIdList);
        boolean result = siteMessagesService.sendSiteMessage(siteMessageRequest);
        return toAjax(result);
    }

}
