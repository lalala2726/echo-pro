package cn.zhangchuangla.system.message.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 新消息通知数据传输对象。
 *
 * <p>用于通过 WebSocket/STOMP 推送给前端以提示用户有新消息产生。</p>
 *
 * @author Chuang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "新消息通知")
public class NewMessageNoticeDTO {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 标题摘要
     */
    @Schema(description = "标题摘要")
    private String title;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String type;

    /**
     * 消息等级
     */
    @Schema(description = "消息等级")
    private String level;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishTime;

}


