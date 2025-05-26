package cn.zhangchuangla.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/26 21:09
 */
@Schema(name = "用户发送消息请求对象", description = "用于普通用户发送消息的请求对象")
@Data
public class UserSendMessageRequest {

    /**
     * 标题
     */
    @Schema(description = "消息标题", type = "string", example = "开发进度")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "消息内容", type = "string", example = "消息内容示例")
    private String content;

    /**
     * 接收者ID列表
     */
    @Schema(description = "接收者ID列表", type = "array<int64>", example = "[1,2,3]")
    private List<Long> receiveId;

}
