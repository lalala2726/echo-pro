package cn.zhangchuangla.message.model.request;

import cn.zhangchuangla.common.core.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/25 22:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "用户消息已发送列表查询参数", description = "用于用户查询自己的已发送的消息时候的查询条件")
public class SentMessageListQueryRequest extends BasePageRequest {

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date sentTime;


}
