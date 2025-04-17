package cn.zhangchuangla.common.core.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 19:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableDataResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -8909765534058591928L;


    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页记录数
     */
    private long pageSize;


    /**
     * 当前时间戳
     */
    private long currentTime;

    /**
     * 列表数据
     */
    private List<?> list;


    /**
     * 分页构造器
     *
     * @param page 分页对象
     */
    public TableDataResult(Page<?> page) {
        this.total = page.getTotal();
        this.pageNum = page.getCurrent();
        this.pageSize = page.getSize();
        this.list = page.getRecords();
        this.currentTime = System.currentTimeMillis();
    }

    /**
     * 默认成功构造器
     *
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @param list     列表数据
     */
    public TableDataResult(long total, long pageNum, long pageSize, List<?> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.currentTime = System.currentTimeMillis();
    }

    /**
     * 空数据构造器，默认返回成功状态
     */
    public static TableDataResult empty() {
        return new TableDataResult(0, 1, 10, Collections.emptyList());
    }
}
