package cn.zhangchuangla.api.controller.message;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.message.model.request.SiteMessageRequest;
import cn.zhangchuangla.message.service.SiteMessagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public SiteMessagesMangeController(SiteMessagesService siteMessagesService) {
        this.siteMessagesService = siteMessagesService;
    }


    /**
     * 发送站内信
     *
     * @param siteMessageRequest 站内信信息
     * @return 操作结果
     */
    @PostMapping("/send")
    @Operation(summary = "发送站内信")
    @PreAuthorize("@auth.hasPermission('message:site:send')")
    @Log(title = "发送站内信", businessType = BusinessType.SEND_MESSAGES)
    public AjaxResult sendSiteMessage(@RequestBody @Validated SiteMessageRequest siteMessageRequest) {
        boolean result = siteMessagesService.sendSiteMessage(siteMessageRequest);
        return toAjax(result);
    }
}
