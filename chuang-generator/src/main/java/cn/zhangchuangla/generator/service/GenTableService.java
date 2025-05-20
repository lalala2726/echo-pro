package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
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
    Page<GenTable> listGenTable(GenTableQueryRequest request);

    /**
     * 查询当前数据库表结构
     *
     * @param databaseTableQueryRequest 查询参数
     * @return 数据库表结构
     */
    Page<DatabaseTable> listDatabaseTables(DatabaseTableQueryRequest databaseTableQueryRequest);

}
