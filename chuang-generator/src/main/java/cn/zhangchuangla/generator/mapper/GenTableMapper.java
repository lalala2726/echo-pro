package cn.zhangchuangla.generator.mapper;

import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
public interface GenTableMapper extends BaseMapper<GenTable> {

    /**
     * 分页查询低代码表
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 分页结果
     */
    Page<GenTable> listGenTable(Page<GenTable> page, @Param("request") GenTableQueryRequest request);

    /**
     * 分页查询当前数据库中的表信息
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 分页结果
     */
    Page<DatabaseTable> listDatabaseTables(Page<DatabaseTable> page, @Param("request") DatabaseTableQueryRequest request);

    /**
     * 根据表名查询数据库表信息
     *
     * @param tableNames 表名列表
     * @return 数据库表信息
     */
    List<DatabaseTable> selectDatabaseTablesByNames(@Param("tableNames") List<String> tableNames);

    /**
     * 根据表名查询表字段信息
     *
     * @param tableName 表名
     * @return 表字段信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(@Param("tableName") String tableName);
}




