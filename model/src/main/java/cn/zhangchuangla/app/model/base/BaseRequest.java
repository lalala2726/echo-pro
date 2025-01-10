package cn.zhangchuangla.app.model.base;

import lombok.Data;

/**
 * 基础请求对象
 */
@Data
public class BaseRequest {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 请求时间戳
     */
    private Long timestamp;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页显示记录数
     */
    private Integer pageSize = 10;
}
