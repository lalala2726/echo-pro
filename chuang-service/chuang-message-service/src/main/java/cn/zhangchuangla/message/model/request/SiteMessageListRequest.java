package cn.zhangchuangla.message.model.request;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/26 22:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "站内信列表请求参数")
public class SiteMessageListRequest extends BasePageRequest {

    /**
     * 是否已读
     */
    @Schema(description = "是否已读", defaultValue = "0")
    private Integer isRead = 0;


}
