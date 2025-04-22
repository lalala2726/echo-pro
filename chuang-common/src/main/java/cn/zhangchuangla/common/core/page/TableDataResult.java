package cn.zhangchuangla.common.core.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
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
     * 列表数据
     */
    private List<?> rows;

}

