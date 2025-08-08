package cn.zhangchuangla.system.core.model.request.notice;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告查询请求
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "公告查询请求对象", description = "公告查询请求对象")
public class SysNoticeQueryRequest extends BasePageRequest {

    /**
     * 公告标题
     */
    @Schema(description = "公告标题", example = "系统维护", type = "string")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @Schema(description = "公告类型（1通知 2公告）", example = "1", type = "string")
    private String noticeType;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin", type = "string")
    private String createBy;
}
