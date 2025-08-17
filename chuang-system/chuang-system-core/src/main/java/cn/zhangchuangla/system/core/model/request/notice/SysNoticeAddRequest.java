package cn.zhangchuangla.system.core.model.request.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 公告添加请求
 *
 * @author Chuang
 */
@Data
@Schema(name = "公告添加请求对象", description = "公告添加请求对象")
public class SysNoticeAddRequest {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题不能超过100个字符")
    @Schema(description = "公告标题", example = "系统维护通知", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String noticeTitle;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    @Size(max = 20000, message = "公告内容不能超过2000个字符")
    @Schema(description = "公告内容", example = "系统将于今晚进行维护，请提前保存工作", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String noticeContent;

    /**
     * 公告类型（1通知 2公告）
     */
    @Schema(description = "公告类型（1通知 2公告）", example = "1", type = "string")
    private String noticeType;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    @Schema(description = "备注", example = "重要通知", type = "string")
    private String remark;
}
