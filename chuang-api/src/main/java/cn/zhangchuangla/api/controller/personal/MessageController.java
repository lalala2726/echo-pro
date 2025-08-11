package cn.zhangchuangla.api.controller.personal;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.system.message.model.dto.UserMessageDto;
import cn.zhangchuangla.system.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.system.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.system.message.model.vo.user.UserMessageListVo;
import cn.zhangchuangla.system.message.model.vo.user.UserMessageVo;
import cn.zhangchuangla.system.message.push.MessagePushService;
import cn.zhangchuangla.system.message.service.MessageQueryService;
import cn.zhangchuangla.system.message.service.SysMessageService;
import cn.zhangchuangla.system.message.service.UserMessageReadService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的消息控制器。
 *
 * @author Chuang
 */
@RestController
@Tag(name = "我的消息", description = "我的消息的阅读和发送等操作")
@RequiredArgsConstructor
@RequestMapping("/personal/message")
public class MessageController extends BaseController {

    private final SysMessageService sysMessageService;
    private final MessageQueryService messageQueryService;
    private final UserMessageReadService userMessageReadService;
    private final MessagePushService messagePushService;

    /**
     * 获取用户消息列表。
     *
     * <p>支持分页与标题、类型、等级、发送人筛选。</p>
     *
     * @param request 查询与分页参数
     * @return 表格数据，包含列表与扩展的已读/未读统计
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户消息列表")
    public AjaxResult<TableDataResult> listUserMessageList(@Parameter(description = "消息列表查询，包含分页和筛选条件")
                                                           @ParameterObject UserMessageListQueryRequest request) {
        Page<UserMessageDto> sysMessagePage = messageQueryService.listUserMessageList(request);
        UserMessageReadCountDto userMessageReadCountDto = messageQueryService.getUserMessageReadCount();
        Map<String, Object> extra = new HashMap<>();
        extra.put("read", userMessageReadCountDto.getRead());
        extra.put("unread", userMessageReadCountDto.getUnRead());
        List<UserMessageListVo> userMessageListVos = copyListProperties(sysMessagePage, UserMessageListVo.class);
        return getTableData(sysMessagePage, userMessageListVos, extra);
    }


    /**
     * 根据消息ID获取消息详情。
     *
     * <p>成功请求后系统会自动将该消息标记为已读，并推送徽标数量变更。</p>
     *
     * @param id 消息ID
     * @return 消息详情
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "根据消息ID获取消息详情，当成功请求此接口后系统将自动将消息标记已读，无须单独调用已读消息的接口")
    public AjaxResult<UserMessageVo> getMessageDetailById(
            @Parameter(description = "消息ID，用于查询消息详情") @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "消息ID必须大于0！");
        UserMessageVo userMessageVo = messageQueryService.getUserMessageDetail(id);
        if (userMessageVo == null) {
            return error("消息不存在");
        }
        pushMessageCount(getUserId());
        return success(userMessageVo);
    }

    /**
     * 获取当前用户消息数量统计。
     *
     * @return 已读与未读数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取用户消息数量")
    public AjaxResult<UserMessageReadCountDto> getUnreadCount() {
        UserMessageReadCountDto userMessageReadCountDto = messageQueryService.getUserMessageReadCount();
        return success(userMessageReadCountDto);
    }

    /**
     * 批量标记消息为已读。
     *
     * <p>仅修改阅读状态，不记录阅读时间。</p>
     *
     * @param ids 消息ID列表
     * @return 操作结果
     */
    @PutMapping("/read/{ids:[\\d,]+}")
    @Operation(summary = "标记消息为已读")
    public AjaxResult<Void> markMessageAsRead(
            @Parameter(description = "消息ID，用于标记消息为已读,通常情况下此接口只是在用户选中多个消息标记已读") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "消息ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "消息ID必须大于0！");
        Long userId = getUserId();
        boolean result = userMessageReadService.batchMarkAsRead(userId, ids);
        pushMessageCount(getUserId());
        return toAjax(result);
    }

    /**
     * 批量标记消息为未读。
     *
     * <p>保留历史阅读时间记录。</p>
     *
     * @param ids 消息ID列表
     * @return 操作结果
     */
    @PutMapping("/unread/{ids:[\\d,]+}")
    @Operation(summary = "标记消息为未读")
    public AjaxResult<Void> markMessageAsUnRead(
            @Parameter(description = "消息ID，用于标记消息为未读") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "消息ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "消息ID必须大于0！");
        Long userId = getUserId();
        boolean result = userMessageReadService.unread(userId, ids);
        pushMessageCount(userId);
        return toAjax(result);
    }

    /**
     * 批量删除消息。
     *
     * @param ids 消息ID集合
     * @return 操作结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除消息")
    public AjaxResult<Void> deleteMessages(@PathVariable("ids") List<Long> ids) {
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "消息ID必须大于0！");
        boolean result = sysMessageService.deleteMessages(ids);
        pushMessageCount(getUserId());
        return toAjax(result);
    }

    /**
     * 推送消息数量
     *
     * @param userId 用户ID
     */
    private void pushMessageCount(Long userId) {
        UserMessageReadCountDto userMessageReadCount = messageQueryService.getUserMessageReadCount();
        messagePushService.pushMessageReadCount(userId, userMessageReadCount);
    }

}
