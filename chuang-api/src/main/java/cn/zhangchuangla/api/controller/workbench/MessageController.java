package cn.zhangchuangla.api.controller.workbench;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.message.model.dto.UserMessageDto;
import cn.zhangchuangla.message.model.dto.UserMessageReadCountDto;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.model.request.SentMessageListQueryRequest;
import cn.zhangchuangla.message.model.request.UserMessageListQueryRequest;
import cn.zhangchuangla.message.model.vo.UserMessageListVo;
import cn.zhangchuangla.message.model.vo.UserMessageVo;
import cn.zhangchuangla.message.model.vo.UserSentMessageListVo;
import cn.zhangchuangla.message.service.SysMessageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

    /**
     * 获取用户消息列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户消息列表")
    public AjaxResult<TableDataResult> listUserMessageList(@Parameter(description = "消息列表查询，包含分页和筛选条件")
                                                           UserMessageListQueryRequest request) {
        Page<UserMessageDto> sysMessagePage = sysMessageService.listUserMessageList(request);
        UserMessageReadCountDto userMessageReadCountDto = sysMessageService.getUserMessageReadCount();
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
    @GetMapping("/{id}")
    @Operation(summary = "根据消息ID获取消息详情，当成功请求此接口后系统将自动将消息标记已读，无须单独调用已读消息的接口")
    public AjaxResult<UserMessageVo> getMessageById(@Parameter(description = "消息ID，用于查询消息详情")
                                                    @PathVariable("id") Long id) {
        checkParam(id == null || id <= 0, "消息ID不能小于等于0");
        SysMessage sysMessage = sysMessageService.getCurrentUserMessageById(id);
        UserMessageVo userMessageVo = new UserMessageVo();
        BeanUtils.copyProperties(sysMessage, userMessageVo);
        return success(userMessageVo);
    }


    /**
     * 获取已发送消息列表
     *
     * @param request 查询参数
     * @return 消息列表
     */
    @GetMapping("/sent/list")
    @Operation(summary = "获取已发送消息列表")
    public AjaxResult<TableDataResult> getSentMessageList(@Parameter(description = "已发送消息列表查询，包含分页和筛选条件")
                                                          SentMessageListQueryRequest request) {
        Page<SysMessage> sysMessagePage = sysMessageService.listUserSentMessageList(request);
        List<UserSentMessageListVo> userSentMessageListVoList = copyListProperties(sysMessagePage, UserSentMessageListVo.class);
        return getTableData(sysMessagePage, userSentMessageListVoList);
    }

    /**
     * 获取用户消息数量
     *
     * @return 用户消息数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取用户消息数量")
    public AjaxResult<UserMessageReadCountDto> getUserMessageReadCount() {
        UserMessageReadCountDto userMessageReadCountDto = sysMessageService.getUserMessageReadCount();
        return success(userMessageReadCountDto);
    }

    /**
     * 标记消息为已读
     *
     * @param ids 消息ID列表
     * @return 操作结果
     */
    @PutMapping("/read/{ids}")
    @Operation(summary = "标记消息为已读")
    public AjaxResult<Void> markMessageAsRead(@Parameter(description = "消息ID，用于标记消息为已读,通常情况下此接口只是在用户选中多个消息标记已读")
                                              @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> checkParam(id == null || id <= 0, "消息ID不能小于等于0"));
        boolean result = sysMessageService.markMessageAsRead(ids);
        return toAjax(result);
    }

    /**
     * 标记消息为未读
     *
     * @param ids 消息ID列表
     * @return 操作结果
     */
    @PutMapping("/unread/{ids}")
    @Operation(summary = "标记消息为未读")
    public AjaxResult<Void> markMessageAsUnRead(@Parameter(description = "消息ID，用于标记消息为未读")
                                                @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> checkParam(id == null || id <= 0, "消息ID不能小于等于0"));
        boolean result = sysMessageService.markMessageAsUnRead(ids);
        return toAjax(result);
    }

}
