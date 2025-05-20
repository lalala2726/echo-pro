package cn.zhangchuangla.generator.mapper;

import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

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
}




