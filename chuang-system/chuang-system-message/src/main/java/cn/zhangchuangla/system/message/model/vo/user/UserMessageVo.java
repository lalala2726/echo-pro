package cn.zhangchuangla.system.message.model.vo.user;

import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * 用户消息视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "用户消息视图对象", description = "用于用户查询自己消息详情")
public class UserMessageVo {

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
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    private String senderName;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
    private Date sentTime;

    /**
     * 上一条消息ID
     */
    @Schema(description = "上一条消息ID")
    private Long previousId;

    /**
     * 下一条消息ID
     */
    @Schema(description = "下一条消息ID")
    private Long nextId;

}
