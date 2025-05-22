package cn.zhangchuangla.common.core.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 11:30
 */
public class PageUtils {


    /**
     * 通用分页封装方法
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param total    总记录数
     * @param data     具体数据集合
     * @param <T>      数据类型
     * @return 分页后的 Page 对象
     */
    public static <T> Page<T> getPage(int pageNum, int pageSize, int total, List<T> data) {
        Page<T> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page.setTotal(total);
        page.setRecords(data);
        return page;
    }
}
