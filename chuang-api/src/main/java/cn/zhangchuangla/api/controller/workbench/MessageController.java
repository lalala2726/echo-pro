package cn.zhangchuangla.api.controller.workbench;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.message.model.dto.UserMessageDto;
import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.message.model.vo.user.UserMessageListVo;
import cn.zhangchuangla.message.model.vo.user.UserMessageVo;
import cn.zhangchuangla.message.service.MessageQueryService;
import cn.zhangchuangla.message.service.SysMessageService;
import cn.zhangchuangla.message.service.UserMessageReadService;
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
 * @author Chuang
 * <p>
 * created on 2025/5/26 19:17
 */
@RestController
@Tag(name = "站内信", description = "站内信的阅读和发送等操作")
@RequiredArgsConstructor
@RequestMapping("/workbench/message")
public class MessageController extends BaseController {

    private final SysMessageService sysMessageService;
    private final MessageQueryService messageQueryService;
    private final UserMessageReadService userMessageReadService;

    /**
     * 获取用户消息列表
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
     * 根据消息ID获取消息详情
     *
     * @param id 消息ID
     * @return 消息详情
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "根据消息ID获取消息详情，当成功请求此接口后系统将自动将消息标记已读，无须单独调用已读消息的接口")
    public AjaxResult<UserMessageVo> getMessageById(
            @Parameter(description = "消息ID，用于查询消息详情") @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "消息ID必须大于0！");
        UserMessageVo userMessageVo = messageQueryService.getUserMessageDetail(id);
        if (userMessageVo == null) {
            return error("消息不存在");
        }
        // 真实阅读，记录首次和最后阅读时间
        Long userId = getUserId();
        userMessageReadService.realRead(userId, id);
        return success(userMessageVo);
    }

    /**
     * 获取用户消息数量
     *
     * @return 用户消息数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取用户消息数量")
    public AjaxResult<UserMessageReadCountDto> getUserMessageReadCount() {
        UserMessageReadCountDto userMessageReadCountDto = messageQueryService.getUserMessageReadCount();
        return success(userMessageReadCountDto);
    }

    /**
     * 标记消息为已读
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
        // 批量标记已读，不记录阅读时间
        Long userId = getUserId();
        boolean result = userMessageReadService
                .batchMarkAsRead(userId, ids);

        return toAjax(result);
    }

    /**
     * 标记消息为未读
     *
     * @param ids 消息ID列表
     * @return 操作结果
     */
    @PutMapping("/unread/{ids:[\\d,]+}")
    @Operation(summary = "标记消息为未读")
    public AjaxResult<Void> markMessageAsUnRead(
            @Parameter(description = "消息ID，用于标记消息为未读") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "部门ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "部门ID必须大于0！");
        // 标记未读，保留阅读时间记录
        Long userId = getUserId();
        boolean result = userMessageReadService.unread(userId, ids);

        return toAjax(result);
    }

    /**
     * 删除消息
     *
     * @param ids 消息ID集合
     * @return 操作结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除消息")
    public AjaxResult<Void> deleteMessages(@PathVariable("ids") List<Long> ids) {
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "消息ID必须大于0！");
        boolean result = sysMessageService.deleteMessages(ids);
        return toAjax(result);
    }

}
