package cn.zhangchuangla.generator.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.mapper.GenTableColumnMapper;
import cn.zhangchuangla.generator.mapper.GenTableMapper;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenConfigUpdateRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import cn.zhangchuangla.generator.service.GenTableService;
import cn.zhangchuangla.generator.utils.GenUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable>
        implements GenTableService {

    private final GenTableMapper genTableMapper;
    private final GenTableColumnMapper genTableColumnMapper;
    private final RedisCache redisCache;

    /**
     * 获取低代码表列表
     *
     * @param request 查询参数
     * @return 列表
     */
    @Override
    public Page<GenTable> listGenTable(GenTableQueryRequest request) {
        Page<GenTable> page = new Page<>(request.getPageNum(), request.getPageSize());
        return genTableMapper.listGenTable(page, request);
    }

    /**
     * 查询当前数据库表信息
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<DatabaseTable> listDatabaseTables(DatabaseTableQueryRequest request) {
        Page<DatabaseTable> page = new Page<>(request.getPageNum(), request.getPageSize());
        return genTableMapper.listDatabaseTables(page, request);
    }

    /**
     * 将当前数据库中的表导入到低代码表中
     *
     * @param tableNames 表名称集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importTable(List<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "表名列表不能为空");
        }

        // 1. 获取已有的低代码表，检查是否有重复
        List<GenTable> existingTables = lambdaQuery().select(GenTable::getTableName).list();
        Set<String> existingTableNames = existingTables.stream()
                .map(GenTable::getTableName)
                .collect(Collectors.toSet());

        for (String tableName : tableNames) {
            if (existingTableNames.contains(tableName)) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "表 " + tableName + " 已存在于代码生成列表中");
            }
        }

        // 2. 查询要导入的数据库表信息
        List<DatabaseTable> dbTables = genTableMapper.selectDatabaseTablesByNames(tableNames);
        if (dbTables == null || dbTables.isEmpty()) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "未找到要导入的数据库表");
        }

        // 3. 获取代码生成配置
        GenConfig config = getConfigInfo();

        // 4. 处理每个表及其列信息
        for (DatabaseTable dbTable : dbTables) {
            // 5. 转换为GenTable并保存
            GenTable genTable = convertToGenTable(dbTable, config);
            save(genTable);

            // 6. 获取表的列信息
            List<GenTableColumn> columns = genTableMapper.selectDbTableColumnsByName(dbTable.getTableName());

            // 7. 处理列信息并保存
            for (GenTableColumn column : columns) {
                // 设置关联的表ID
                column.setTableId(genTable.getTableId());

                // 处理Java字段名、类型等
                processColumnInfo(column);

                genTableColumnMapper.insert(column);
            }
        }

        return true;
    }

    /**
     * 处理表列信息
     */
    private void processColumnInfo(GenTableColumn column) {
        // 设置Java字段名（驼峰命名）
        String javaField = GenUtils.toCamelCase(column.getColumnName());
        column.setJavaField(javaField);

        // 设置Java类型
        String javaType = GenUtils.getJavaType(column.getColumnType());
        column.setJavaType(javaType);

        // 设置表单显示类型
        String htmlType = GenUtils.getHtmlType(column.getColumnType());
        column.setHtmlType(htmlType);

        // 设置默认值
        if ("1".equals(column.getIsPk())) {
            column.setIsInsert("1");
            column.setIsEdit("0");
            column.setIsList("0");
            column.setIsQuery("0");
        } else {
            column.setIsInsert("1");
            column.setIsEdit("1");
            column.setIsList("1");

            // 设置查询条件
            if (!GenUtils.isExcludeField(column.getColumnName())) {
                column.setIsQuery("1");
                // 默认等于查询
                column.setQueryType("EQ");
            } else {
                column.setIsQuery("0");
            }
        }
    }

    /**
     * 将数据库表转换为代码生成表
     */
    private GenTable convertToGenTable(DatabaseTable dbTable, GenConfig config) {
        GenTable genTable = new GenTable();

        genTable.setTableName(dbTable.getTableName());
        genTable.setTableComment(dbTable.getTableComment());
        // 默认模板类型
        genTable.setTplCategory("crud");

        // 设置类名（首字母大写的驼峰命名）
        String className = GenUtils.convertClassName(dbTable.getTableName());
        genTable.setClassName(className);

        // 设置包名和模块名
        genTable.setPackageName(config.getPackageName());
        genTable.setModuleName(GenUtils.getModuleName(config.getPackageName()));

        // 设置业务名和功能名
        genTable.setBusinessName(GenUtils.getBusinessName(dbTable.getTableName()));
        genTable.setFunctionName(StrUtil.isBlank(dbTable.getTableComment()) ?
                dbTable.getTableName() : dbTable.getTableComment());

        // 设置作者
        genTable.setFunctionAuthor(config.getAuthor());

        // 设置创建者
        genTable.setCreateBy(SecurityUtils.getUsername());

        return genTable;
    }

    /**
     * 获取低代码表的配置信息，优先从 Redis 缓存中获取，若不存在则创建一个默认配置并缓存。
     *
     * @return 返回当前的低代码表配置信息，始终不为 null。
     */
    @Override
    public GenConfig getConfigInfo() {
        String cacheKey = RedisConstants.Generator.CONFIG_INFO;
        GenConfig genConfigCache = redisCache.getCacheObject(cacheKey);
        if (genConfigCache == null) {
            GenConfig genConfig = new GenConfig();
            redisCache.setCacheObject(cacheKey, genConfig);
            return genConfig;
        }
        return genConfigCache;
    }

    /**
     * 修改配置信息
     *
     * @param request 新配置信息
     * @return 操作结果
     */
    @Override
    public boolean updateConfigInfo(GenConfigUpdateRequest request) {
        GenConfig genConfig = new GenConfig();
        BeanUtils.copyProperties(request, genConfig);
        redisCache.setCacheObject(RedisConstants.Generator.CONFIG_INFO, genConfig);
        return true;
    }

    /**
     * 根据ID查询代码生成表信息
     *
     * @param id id
     * @return 代码生成表信息
     */
    @Override
    public GenTable getGenTableById(Long id) {
        return getById(id);
    }

    /**
     * 根据表名查询表字段配置
     *
     * @param tableName 表名
     * @return 表字段配置列表
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableName(String tableName) {
        return genTableColumnMapper.selectDbTableColumnsByTableName(tableName);
    }
}




