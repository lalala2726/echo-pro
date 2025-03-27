package cn.zhangchuangla.message.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户
 */
@Data
@Schema(description = "消息接收视图类")
public class SiteMessagesVo {
    /**
     *
     */
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String messageType;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createdTime;

}
