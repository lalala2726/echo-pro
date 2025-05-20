package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.GenTableListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
public interface GenTableService extends IService<GenTable> {

    /**
     * 分页查询低代码表
     *
     * @param request 查询参数
     * @return 低代码表列表
     */
    Page<GenTable> listGenTable(GenTableListRequest request);

}
