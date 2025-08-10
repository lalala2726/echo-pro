package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.system.message.enums.MessageSendMethodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/24 22:42
 */
@Data
public class SysSendMessageRequest {

    /**
     * 消息发送方式
     */
    @Schema(description = "消息发送方式", allowableValues = {"user", "role", "dept", "all"}, example = "user")
    @NotNull(message = "消息发送方式不能为空")
    private MessageSendMethodEnum receiveType;

    /**
     * 接收者ID列表，根据发送方式指定不同类型的ID：
     */
    @Schema(description = "接收者ID列表（根据发送方式对应：用户ID、角色ID或部门ID）", type = "List<Long>")
    private List<Long> receiveId;


    /**
     * 要发送的消息内容详情
     */
    @Schema(description = "具体的消息内容信息")
    @NotNull(message = "消息内容不能为空")
    private MessageRequest message;

}
