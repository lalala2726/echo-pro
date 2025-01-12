package cn.zhangchuangla.app.model.base;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 11:03
 */
@Data
public class BasePageRequest {

    /**
     * 当前页码
     */
    private Long pageNum = 1L;

    /**
     * 每页数量
     */
    private Long pageSize = 10L;
}
