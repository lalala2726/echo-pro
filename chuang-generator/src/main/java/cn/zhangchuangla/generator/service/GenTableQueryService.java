package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.model.entity.GenTable;

/**
 * 代码生成表查询服务接口
 *
 * @author Chuang
 */
public interface GenTableQueryService {

    /**
     * 根据表ID查询代码生成表配置，包含列信息
     *
     * @param id 表ID
     * @return GenTable 表配置信息，包含列
     */
    GenTable selectGenTableById(Long id);

    /**
     * 根据表名查询代码生成表配置，包含列信息
     *
     * @param tableName 表名
     * @return GenTable 表配置信息，包含列
     */
    GenTable selectGenTableByName(String tableName);
}
