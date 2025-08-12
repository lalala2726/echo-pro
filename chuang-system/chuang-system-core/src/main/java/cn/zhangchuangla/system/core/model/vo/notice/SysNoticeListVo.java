package cn.zhangchuangla.system.core.model.vo.notice;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 公告列表视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "公告列表视图对象", description = "用于展示公告列表的视图对象")
public class SysNoticeListVo {

    /**
     * 公告ID
     */
    @Schema(description = "公告ID", type = "number", example = "1")
    @Excel(name = "公告ID")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题", type = "string", example = "系统通知")
    @Excel(name = "公告标题")
    private String noticeTitle;

    /**
     * 公告类型
     */
    @Schema(description = "公告类型", type = "string", example = "1")
    @Excel(name = "公告类型")
    private String noticeType;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string", example = "2021-01-01 00:00:00")
    @Excel(name = "创建时间")
    private Date createTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者", type = "string", example = "admin")
    @Excel(name = "创建者")
    private String createBy;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", example = "无")
    @Excel(name = "备注")
    private String remark;
}
