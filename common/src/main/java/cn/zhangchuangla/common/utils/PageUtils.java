package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.exception.ParamException;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 11:30
 */
public class PageUtils {

    /**
     * 合法性校验
     */
    public static void checkPageParams(Long pageNum, Long pageSize) {
        if (pageNum == null || pageNum < 1) {
            throw new ParamException("pageNum参数不合法");
        }
        if (pageSize == null || pageSize < 1) {
            throw new ParamException("pageSize参数不合法");
        }
    }
}
