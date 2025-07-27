package cn.zhangchuangla.common.core.entity.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/27 20:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T> {

    /**
     * 当前页码
     */
    public Long pageNum;

    /**
     * 每页记录数
     */
    public Long pageSize;

    /**
     * 总记录数
     */
    public Long total;

    /**
     * 列表数据
     */
    public List<T> rows;
}
