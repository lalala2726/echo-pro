package cn.zhangchuangla.app.model.base;

import lombok.Data;

import java.util.Date;

/**
 * 基础视图对象
 */
@Data
public class BaseVO {

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
