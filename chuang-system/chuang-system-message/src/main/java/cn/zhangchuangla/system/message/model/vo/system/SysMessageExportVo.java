package cn.zhangchuangla.system.message.model.vo.system;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统消息导出列表
 *
 * @author Chuang
 */
@Data
@Schema(description = "系统消息导出列表")
public class SysMessageExportVo {

    /**
     * 消息ID
     */
    @Excel(name = "消息ID", sort = 0)
    private Long id;

    /**
     * 消息标题
     */
    @Excel(name = "消息标题", sort = 1)
    private String title;

    /**
     * 消息类型
     */
    @Excel(name = "消息类型", sort = 3)
    private String type;

    /**
     * 消息级别
     */
    @Excel(name = "消息级别", sort = 4)
    private String level;

    /**
     * 发送者姓名
     */
    @Excel(name = "发送者姓名", sort = 6)
    private String senderName;

    /**
     * 目标类型
     */
    @Excel(name = "目标类型", sort = 7)
    private String targetType;

    /**
     * 发布时间
     */
    @Excel(name = "发布时间", sort = 12)
    private Date publishTime;

}
