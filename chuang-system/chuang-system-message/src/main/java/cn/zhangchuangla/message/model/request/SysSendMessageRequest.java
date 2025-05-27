package cn.zhangchuangla.message.model.request;

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
     * 消息发送方式，取值说明：
     * 0 - 按用户ID发送
     * 1 - 按角色发送
     * 2 - 按部门发送
     */
    @Schema(description = "消息发送方式（0-按用户ID接收，1-按角色ID接收，2-按部门ID接收，3-全部用户接收）", type = "integer", example = "0")
    @NotNull(message = "消息发送方式不能为空")
    private Integer sendMethod;

    /**
     * 接收者ID列表，根据发送方式指定不同类型的ID：
     * 发送方式为0时是用户ID列表
     * 发送方式为1时是角色ID列表
     * 发送方式为2时是部门ID列表
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
