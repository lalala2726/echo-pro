package cn.zhangchuangla.system.message.model.vo.system;

import cn.zhangchuangla.common.excel.annotation.Excel;
import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageReceiveTypeEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统消息表列表视图对象
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表列表视图对象")
public class SysMessageListVo {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    @Excel(name = "消息ID", sort = 0)
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    @Excel(name = "消息标题", sort = 1)
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    @Excel(name = "消息内容", sort = 2)
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    @Excel(name = "消息类型", sort = 3)
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别")
    @Excel(name = "消息级别", sort = 4)
    private MessageLevelEnum level;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    @Excel(name = "发送者姓名", sort = 6)
    private String senderName;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型")
    @Excel(name = "目标类型", sort = 7)
    private MessageReceiveTypeEnum targetType;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    @Excel(name = "发布时间", sort = 12)
    private Date publishTime;

}
