package cn.zhangchuangla.system.core.model.vo.notice;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 公告表
 *
 * @author Chuang
 */
@Data
public class SysNoticeExportVo {

    /**
     * 公告ID
     */
    @Excel(name = "公告ID")
    private Long id;

    /**
     * 公告标题
     */
    @Excel(name = "公告标题")
    private String noticeTitle;

    /**
     * 公告内容
     */
    @Excel(name = "公告内容")
    private String noticeContent;

    /**
     * 公告类型（1通知 2公告）
     */
    @Excel(name = "公告类型")
    private String noticeType;

    /**
     * 创建者
     */
    @Excel(name = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
