package cn.zhangchuangla.system.message.model.vo.system;

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
    @Schema(description = "消息ID", type = "integer", example = "1")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统维护通知")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", type = "string", example = "系统将在今晚进行维护，请提前保存数据。")
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", type = "string", example = "system")
    private String type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别", type = "string", example = "important")
    private String level;

    /**
     * 目标接受者ID列表
     */
    @Schema(description = "目标接受者ID列表", type = "array", example = "[1, 2, 3]")
    private List<Long> targetIds;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名", type = "string", example = "系统管理员")
    private String senderName;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", type = "string", example = "all")
    private String targetType;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间", type = "string", format = "date-time", example = "2025-08-10T10:00:00Z")
    private Date publishTime;


}
