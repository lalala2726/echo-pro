package cn.zhangchuangla.common.result;

import cn.zhangchuangla.common.enums.ResponseCode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 表格分页数据结果
 * 用于封装所有分页相关的数据返回
 *
 * @author Chuang
 * <p>
 * created on 2025/3/20 19:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableDataResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -8909765534058591928L;

    /**
     * 状态码
     */
    @Schema(description = "状态码")
    private Integer code;

    /**
     * 返回消息
     */
    @Schema(description = "返回消息")
    private String message;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳")
    private Long currentTime;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String time;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Long pageNum;

    /**
     * 每页记录数
     */
    @Schema(description = "每页记录数")
    private Long pageSize;

    /**
     * 列表数据
     */
    @Schema(description = "列表数据")
    private List<?> rows;

    /**
     * 默认构造函数，初始化基本属性
     */
    public TableDataResult(List<?> rows, Long total, Long pageSize, Long pageNum) {
        this.code = ResponseCode.SUCCESS.getCode();
        this.message = ResponseCode.SUCCESS.getMessage();
        this.time = getCurrentTime();
        this.currentTime = System.currentTimeMillis();
        this.rows = rows;
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    /**
     * 从 Page 对象构建 TableDataResult
     *
     * @param page 分页对象
     * @return TableDataResult 实例
     */
    public static TableDataResult build(Page<?> page) {
        return new TableDataResult(
                page.getRecords(),
                page.getTotal(),
                page.getSize(),
                page.getCurrent()
        );
    }

    /**
     * 从 Page 对象和自定义行数据构建 TableDataResult
     *
     * @param page 分页对象
     * @param rows 自定义行数据
     * @return TableDataResult 实例
     */
    public static TableDataResult build(Page<?> page, List<?> rows) {
        return new TableDataResult(
                rows,
                page.getTotal(),
                page.getSize(),
                page.getCurrent()
        );
    }

    /**
     * 获取当前时间，格式为 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的字符串
     */
    private static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}

