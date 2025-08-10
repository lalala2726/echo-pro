package cn.zhangchuangla.system.message.model.vo.system;

import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageSendMethodEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 系统消息表视图对象
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表视图对象")
public class SysMessageVo {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别")
    private MessageLevelEnum level;

    /**
     * 目标接受者ID列表
     */
    private List<Long> targetIds;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    private String senderName;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型")
    private MessageSendMethodEnum targetType;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishTime;


}
