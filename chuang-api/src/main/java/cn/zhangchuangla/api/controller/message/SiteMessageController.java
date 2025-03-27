package cn.zhangchuangla.api.controller.message;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.message.model.entity.SiteMessages;
import cn.zhangchuangla.message.model.request.SiteMessageListRequest;
import cn.zhangchuangla.message.model.vo.SiteMessagesUserListVo;
import cn.zhangchuangla.message.model.vo.SiteMessagesVo;
import cn.zhangchuangla.message.service.SiteMessagesService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

        // 转换成前端需要的数据格式
        ArrayList<SiteMessagesUserListVo> siteMessagesUserListVos = new ArrayList<>();
        currentUserSiteMessagesList.getRecords().forEach(siteMessages -> {
            SiteMessagesUserListVo siteMessagesUserListVo = new SiteMessagesUserListVo();
            BeanUtils.copyProperties(siteMessages, siteMessagesUserListVo);
            siteMessagesUserListVos.add(siteMessagesUserListVo);
        });

        return getTableData(currentUserSiteMessagesList, siteMessagesUserListVos);
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
    @GetMapping("/read/{ids}")
    @Operation(summary = "批量标记为已读")
    public AjaxResult isReadSiteMessage(@PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "id不能为空");
        });
        int read = siteMessagesService.isRead(ids);
        return toAjax(read);
    }

}
