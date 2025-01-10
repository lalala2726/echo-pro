package cn.zhangchuangla.common.model.request;

import lombok.Data;

/**
 * 通用请求参数
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:57
 */
@Data
public class BaseRequest {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String orderBy;

}
