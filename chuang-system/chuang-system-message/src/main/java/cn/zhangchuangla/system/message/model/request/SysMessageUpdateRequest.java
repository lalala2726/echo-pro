package cn.zhangchuangla.system.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统消息表修改请求参数
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表修改请求参数")
public class SysMessageUpdateRequest {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    @NotNull(message = "消息ID不能为空")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息")
    @NotNull(message = "消息类型：1-系统消息 2-通知消息 3-公告消息不能为空")
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急")
    @NotNull(message = "消息级别：1-普通 2-重要 3-紧急不能为空")
    private Integer level;
}
