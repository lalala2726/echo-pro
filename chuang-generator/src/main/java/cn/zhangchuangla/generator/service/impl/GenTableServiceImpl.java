package cn.zhangchuangla.generator.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.Constants;
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
import cn.zhangchuangla.generator.model.request.GenTableUpdateRequest;
import cn.zhangchuangla.generator.service.GenTableService;
import cn.zhangchuangla.generator.utils.GenUtils;
import cn.zhangchuangla.generator.utils.VelocityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Chuang
 *         <p>
 *         created on 2025-05-20 11:01
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
     * 处理表列信息并设置字段的生成规则。
     *
     * <p>
     * 此方法用于根据数据库列的名称和类型，自动填充代码生成所需的字段属性，
     * 包括 Java 字段名（驼峰命名）、Java 类型、HTML 表单显示类型以及字段在业务逻辑中的默认行为。
     * </p>
     *
     * <p>
     * 主键字段将被标记为只读（不可编辑），且不会出现在列表展示与查询条件中；
     * 非主键字段则会根据其是否为排除字段（如创建人、创建时间等系统字段）决定是否作为查询条件。
     * </p>
     *
     * @param column 数据库列实体对象，不能为 null
     * @throws IllegalArgumentException 如果传入的 column 为 null
     */
    private void processColumnInfo(GenTableColumn column) {
        if (column == null) {
            throw new IllegalArgumentException("列信息不能为空");
        }
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
        if (Constants.Generator.YES.equals(column.getIsPk())) {
            column.setIsInsert(Constants.Generator.YES);
            column.setIsEdit(Constants.Generator.NO);
            column.setIsList(Constants.Generator.NO);
            column.setIsQuery(Constants.Generator.NO);
        } else {
            column.setIsInsert(Constants.Generator.YES);
            column.setIsEdit(Constants.Generator.YES);
            column.setIsList(Constants.Generator.YES);

            // 设置查询条件
            if (!GenUtils.isExcludeField(column.getColumnName())) {
                column.setIsQuery(Constants.Generator.YES);
                // 默认等于查询
                column.setQueryType(Constants.Generator.EQ);
            } else {
                column.setIsQuery(Constants.Generator.YES);
            }
        }
    }

    /**
     * 将数据库表信息转换为代码生成所需的低代码表实体。
     *
     * <p>
     * 此方法会基于传入的数据库表结构和全局配置，构建一个完整的 GenTable 实体对象，
     * 用于后续的代码生成操作。
     * </p>
     *
     * @param dbTable 数据库表信息，不能为 null
     * @param config  全局代码生成配置，不能为 null
     * @return 返回转换后的 GenTable 实体对象
     * @throws IllegalArgumentException 如果 dbTable 或 config 为 null
     */
    private GenTable convertToGenTable(DatabaseTable dbTable, GenConfig config) {
        if (dbTable == null || config == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        GenTable genTable = new GenTable();

        // 基本信息设置
        genTable.setTableName(dbTable.getTableName());
        genTable.setTableComment(dbTable.getTableComment());
        // 默认模板类型为 CRUD 操作
        genTable.setTplCategory(Constants.Generator.crud);

        // 类名处理：将表名转换为首字母大写的驼峰命名
        String className = GenUtils.convertClassName(dbTable.getTableName());
        genTable.setClassName(className);

        // 包路径和模块名设置
        genTable.setPackageName(config.getPackageName());
        genTable.setModuleName(GenUtils.getModuleName(config.getPackageName()));

        // 业务信息设置
        genTable.setBusinessName(GenUtils.getBusinessName(dbTable.getTableName()));
        genTable.setFunctionName(
                StrUtil.isBlank(dbTable.getTableComment()) ? dbTable.getTableName() : dbTable.getTableComment());

        // 设置作者信息
        genTable.setFunctionAuthor(config.getAuthor());

        // 设置创建者（从安全上下文中获取当前用户名）
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
        // 先查询表信息，获取表ID
        GenTable table = lambdaQuery().eq(GenTable::getTableName, tableName).one();
        if (table == null) {
            return new ArrayList<>();
        }

        // 根据表ID查询表字段信息
        return genTableColumnMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GenTableColumn>()
                        .eq(GenTableColumn::getTableId, table.getTableId())
                        .orderByAsc(GenTableColumn::getSort));
    }

    /**
     * 预览代码
     *
     * <p>
     * 此方法用于预览指定表名的代码生成结果。它会查询该低代码表的信息以及相关的列信息，
     * 然后使用Velocity模板引擎渲染出所有相关文件的代码内容。
     * </p>
     *
     * <p>
     * 如果找不到对应的表或表没有列信息，将抛出异常。
     * </p>
     *
     * @param tableName 表名，不能为空且必须对应一个已存在的低代码表
     * @return 返回一个包含文件路径和对应代码内容的映射表，其中键是文件路径，值是生成的代码字符串
     * @throws ServiceException 如果表不存在或表没有列信息
     */
    @Override
    public Map<String, String> previewCode(String tableName) {
        // 查询表信息
        GenTable table = lambdaQuery().eq(GenTable::getTableName, tableName).one();
        if (table == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "表不存在");
        }

        // 查询列信息
        List<GenTableColumn> columns = selectGenTableColumnListByTableName(tableName);
        if (columns.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "表没有列信息");
        }

        // 设置列信息
        table.setColumns(columns);

        // 初始化Velocity引擎
        VelocityUtils.initVelocity();

        // 设置模板变量信息
        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList();

        // 生成代码
        Map<String, String> codeMap = new HashMap<>();
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, StandardCharsets.UTF_8.name());
            tpl.merge(context, sw);

            // 获取文件名
            String fileName = VelocityUtils.getFileName(template, table);

            // 添加到返回结果
            codeMap.put(fileName, sw.toString());
        }

        return codeMap;
    }

    /**
     * 下载生成的代码压缩包
     *
     * <p>
     * 此方法会预览指定表名的代码内容，并将其打包为 ZIP 格式供下载。
     * </p>
     *
     * @param tableName 表名，不能为空且必须对应一个已存在的低代码表
     * @return 生成的代码压缩包字节数组，可用于下载
     * @throws ServiceException 如果表不存在、生成代码失败或发生 IO 异常
     */
    @Override
    public byte[] downloadCode(String tableName) {
        // 获取代码预览内容
        Map<String, String> codeMap = previewCode(tableName);

        // 创建内存输出流用于生成ZIP
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(outputStream)) {

            // 设置ZIP文件编码为UTF-8，确保文件名正确
            // 最高压缩级别
            zip.setLevel(9);

            // 将每个文件写入到ZIP中
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                // 规范化文件路径，确保使用标准的"/"分隔符
                String filePath = entry.getKey().replace("\\", "/");

                // 确保文件内容不为空
                String content = entry.getValue();
                if (content == null) {
                    content = "";
                }

                // 创建ZIP条目
                ZipEntry zipEntry = new ZipEntry(filePath);
                zip.putNextEntry(zipEntry);

                // 写入文件内容
                IOUtils.write(content, zip, StandardCharsets.UTF_8);
                zip.flush();
                zip.closeEntry();
            }

            // 确保所有数据都写入
            zip.finish();

            // 返回生成的ZIP字节流
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("生成代码失败，表名：{}", tableName, e);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "生成代码失败，请稍后重试");
        }
    }

    /**
     * 更新低代码表信息
     *
     * @param request 更新请求
     * @return 更新结果
     */
    @Override
    public boolean updateGenTable(GenTableUpdateRequest request) {
        // 检查表是否存在
        GenTable existingTable = getById(request.getTableId());
        if (existingTable == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "表不存在");
        }

        // 创建更新对象
        GenTable updateTable = new GenTable();
        BeanUtils.copyProperties(request, updateTable);

        // 设置更新者
        updateTable.setUpdateBy(SecurityUtils.getUsername());

        // 更新表信息
        return updateById(updateTable);
    }

    /**
     * 删除低代码表，支持批量删除
     *
     * <p>
     * 此方法会级联删除与这些低代码表关联的列信息。
     * </p>
     *
     * @param tableIds 低代码表ID集合，不能为null且不能为空
     * @return 操作结果，成功返回true，否则false
     * @throws ServiceException 如果参数无效或删除过程中发生异常
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public boolean deleteGenTable(List<Long> tableIds) {
        if (tableIds == null || tableIds.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "表ID列表不能为空");
        }

        // 删除主表数据
        removeByIds(tableIds);

        // 删除关联的列数据
        LambdaQueryWrapper<GenTableColumn> columnWrapper = new LambdaQueryWrapper<>();
        columnWrapper.in(GenTableColumn::getTableId, tableIds);
        genTableColumnMapper.delete(columnWrapper);

        return true;
    }
}
