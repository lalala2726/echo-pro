package cn.zhangchuangla.system.message.model.vo.system;

import cn.zhangchuangla.common.excel.annotation.Excel;
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
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息")
    @Excel(name = "消息类型：1-系统消息 2-通知消息 3-公告消息", sort = 3)
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急")
    @Excel(name = "消息级别：1-普通 2-重要 3-紧急", sort = 4)
    private Integer level;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    @Excel(name = "发送者姓名", sort = 6)
    private String senderName;

    /**
     * 目标类型：1-指定用户 2-全部用户 3-角色用户
     */
    @Schema(description = "目标类型：1-指定用户 2-全部用户 3-角色用户")
    @Excel(name = "目标类型：1-指定用户 2-全部用户 3-角色用户", sort = 7)
    private Integer targetType;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    @Excel(name = "发布时间", sort = 12)
    private Date publishTime;

}
