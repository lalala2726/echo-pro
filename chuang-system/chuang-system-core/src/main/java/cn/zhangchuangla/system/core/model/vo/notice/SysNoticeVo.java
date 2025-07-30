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
    @Schema(description = "公告ID")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题")
    private String noticeTitle;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容")
    private String noticeContent;

    /**
     * 公告类型（1通知 2公告）
     */
    @Schema(description = "公告类型（1通知 2公告）")
    private String noticeType;

    /**
     * 公告类型描述
     */
    @Schema(description = "公告类型描述")
    private String noticeTypeDesc;

    /**
     * 公告状态（0正常 1关闭）
     */
    @Schema(description = "公告状态（0正常 1关闭）")
    private Integer status;

    /**
     * 公告状态描述
     */
    @Schema(description = "公告状态描述")
    private String statusDesc;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
