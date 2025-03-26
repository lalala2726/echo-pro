package cn.zhangchuangla.api.controller.message;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.message.model.entity.SiteMessages;
import cn.zhangchuangla.message.model.request.SiteMessageListRequest;
import cn.zhangchuangla.message.model.vo.SiteMessagesUserListVo;
import cn.zhangchuangla.message.service.SiteMessagesService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 站内信
 */
@RestController
@RequestMapping("/message/site")
@Tag(name = "站内信")
public class SiteMessageController extends BaseController {

    private final SiteMessagesService siteMessagesService;

    public SiteMessageController(SiteMessagesService siteMessagesService) {
        this.siteMessagesService = siteMessagesService;
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
        ArrayList<SiteMessagesUserListVo> siteMessagesUserListVos = new ArrayList<>();
        currentUserSiteMessagesList.getRecords().forEach(siteMessages -> {
            SiteMessagesUserListVo siteMessagesUserListVo = new SiteMessagesUserListVo();
            BeanUtils.copyProperties(siteMessages, siteMessagesUserListVo);
            siteMessagesUserListVos.add(siteMessagesUserListVo);
        });
        return getTableData(currentUserSiteMessagesList, siteMessagesUserListVos);
    }
}
