package cn.zhangchuangla.system.core.model.vo.notice;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 公告视图对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "公告视图对象", description = "用于展示公告的视图对象")
public class SysNoticeVo extends BaseVo {

    /**
     * 公告ID
     */
    @Schema(description = "公告ID", type = "number", example = "1")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题", type = "string", example = "系统通知")
    private String noticeTitle;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容", type = "string", example = "系统通知内容")
    private String noticeContent;

    /**
     * 公告类型
     */
    @Schema(description = "公告类型", type = "string", example = "1")
    private String noticeType;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string", example = "2021-01-01 00:00:00")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", type = "string", example = "2021-01-01 00:00:00")
    private Date updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者", type = "string", example = "admin")
    private String createBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者", type = "string", example = "admin")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", example = "无")
    private String remark;
}
