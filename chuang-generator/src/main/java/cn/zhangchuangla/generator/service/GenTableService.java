package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenConfigUpdateRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 导入当前数据库中的表到低代码表中 （支持批量导入）
     *
     * @param tableNames 表名称集合
     * @return 导入结果
     */
    boolean importTable(List<String> tableNames);

    /**
     * 查询配置信息
     *
     * @return 配置信息
     */
    GenConfig getConfigInfo();

    /**
     * 修改配置信息
     *
     * @param request 配置信息
     * @return 修改结果
     */
    boolean updateConfigInfo(GenConfigUpdateRequest request);

    /**
     * 根据ID查询低代码表信息
     *
     * @param id id
     * @return 返回详情
     */
    GenTable getGenTableById(Long id);

    /**
     * 根据表名查询表字段配置
     *
     * @param tableName 表名
     * @return 表字段配置列表
     */
    List<GenTableColumn> selectGenTableColumnListByTableName(String tableName);
}
