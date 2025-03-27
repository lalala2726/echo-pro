package cn.zhangchuangla.api.controller.message;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.message.model.entity.SiteMessages;
import cn.zhangchuangla.message.model.request.SiteMessageListRequest;
import cn.zhangchuangla.message.model.vo.SiteMessagesVo;
import cn.zhangchuangla.message.service.SiteMessagesService;
import cn.zhangchuangla.message.service.UserSiteMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内信
 */
@RestController
@RequestMapping("/message/site")
@Tag(name = "站内信")
public class SiteMessageController extends BaseController {

    private final SiteMessagesService siteMessagesService;
    private final UserSiteMessageService userSiteMessageService;

    public SiteMessageController(SiteMessagesService siteMessagesService, UserSiteMessageService userSiteMessageService) {
        this.siteMessagesService = siteMessagesService;
        this.userSiteMessageService = userSiteMessageService;
    }


    /**
     * 获取当前用户站内信列表
     *
     * @return 返回当前用户站内信列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取当前用户站内信列表")
    public TableDataResult getCurrentUserSiteMessagesList(SiteMessageListRequest request) {
        Page<SiteMessages> currentUserSiteMessagesList = siteMessagesService.getCurrentUserSiteMessagesList(request);
        List<SiteMessagesVo> siteMessagesVos = copyListProperties(currentUserSiteMessagesList, SiteMessagesVo.class);
        return getTableData(currentUserSiteMessagesList, siteMessagesVos);
    }

    /**
     * 查看站内信消息详情
     *
     * @param id 消息ID
     * @return 返回消息详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查看消息详情")
    public AjaxResult getSiteMessageById(@PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "id不能为空");
        SiteMessages siteMessages = siteMessagesService.getSiteMessageById(id);
        SiteMessagesVo siteMessagesVo = new SiteMessagesVo();
        BeanUtils.copyProperties(siteMessages, siteMessagesVo);
        return success(siteMessagesVo);
    }


    /**
     * 批量标记为已读
     *
     * @param ids 消息ID
     * @return 操作结果
     */
    @PutMapping("/read/{ids}")
    @Operation(summary = "批量标记为已读")
    public AjaxResult readSiteMessage(@PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "id不能为空");
        });
        int read = userSiteMessageService.isRead(ids);
        return toAjax(read);
    }

    /**
     * 标记全部已读
     *
     * @return 操作结果
     */
    @PutMapping("/read/all")
    @Operation(summary = "标记全部已读")
    public AjaxResult readAllSiteMessage() {
        return toAjax(userSiteMessageService.isReadAll());
    }

    /**
     * 删除站内信，支持批量删除
     *
     * @param ids 站内信ID
     * @return 返回操作结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除站内信")
    public AjaxResult deleteSiteMessage(@PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "id不能为空");
        });
        return toAjax(userSiteMessageService.deleteSiteMessage(ids));
    }


}
