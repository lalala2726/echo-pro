package cn.zhangchuangla.system.message.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * 用户已发送消息视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "用户消息视图对象", description = "用于用户查询自己已发送消息详情")
public class UserSentMessageListVo {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID", type = "integer", example = "1")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统通知")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", type = "string", example = "您的账户存在异常，请及时处理。")
    private String content;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间", type = "string", format = "date-time", example = "2023-04-01T08:30:00Z")
    private Date sentTime;

}
