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
    @Schema(description = "公告ID")
    @Excel(name = "公告ID")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题")
    @Excel(name = "公告标题")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @Schema(description = "公告类型（1通知 2公告）")
    @Excel(name = "公告类型（1通知 2公告）")
    private String noticeType;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @Excel(name = "创建者")
    private String createBy;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Excel(name = "备注")
    private String remark;
}
