package cn.zhangchuangla.generator.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.generator.model.TableField;
import cn.zhangchuangla.generator.model.TableInfo;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.enums.TableType;
import cn.zhangchuangla.generator.model.request.*;
import cn.zhangchuangla.generator.model.vo.DbTableVO;
import cn.zhangchuangla.generator.model.vo.PreviewCodeVO;
import cn.zhangchuangla.generator.service.GenTableColumnService;
import cn.zhangchuangla.generator.service.GenTableQueryService;
import cn.zhangchuangla.generator.service.GenTableService;
import cn.zhangchuangla.generator.util.GenUtils;
import cn.zhangchuangla.generator.util.VelocityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成业务表 服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenTableServiceImpl extends ServiceImpl<cn.zhangchuangla.generator.mapper.GenTableMapper, GenTable>
        implements GenTableService {

    private final GenTableColumnService genTableColumnService;
    private final GenTableQueryService genTableQueryService;

    @NotNull
    private static TableField getTableField(GenTableColumn column) {
        TableField field = new TableField();
        field.setColumnName(column.getColumnName());
        field.setColumnType(column.getColumnType());
        field.setColumnComment(column.getColumnComment());
        field.setIsPk(column.isPk());
        field.setIsIncrement(column.isIncrement());
        field.setIsRequired(column.isRequired());
        field.setIsList(column.isList());
        field.setIsQuery(column.isQuery());
        field.setQueryType(column.getQueryType());
        field.setHtmlType(column.getHtmlType());
        field.setDictType(column.getDictType());
        return field;
    }

    // 迁移过来的旧 generate 方法，后续会重构为 previewCode, downloadCode, genToPath 等方法的核心逻辑
    private boolean generateCodeLogic(TableInfo tableInfo, String outputDir) throws IOException {
        VelocityUtils.initVelocity(); // 初始化应在应用启动时进行一次

        // 设置表信息 - 这部分信息应该在构建 tableInfo 对象时已经从 GenTable 实体中获取

        String actualOutputDir = outputDir;
        if (StringUtils.isEmpty(actualOutputDir)) {
            // 默认输出到项目根目录，可配置
            actualOutputDir = System.getProperty("user.dir");
        }

        try {
            VelocityUtils.generateEntity(tableInfo, actualOutputDir);
            VelocityUtils.generateRequestClasses(tableInfo, actualOutputDir);
            VelocityUtils.generateMapper(tableInfo, actualOutputDir);
            // 注意路径，通常在 resources 下
            VelocityUtils.generateMapperXml(tableInfo, actualOutputDir);
            VelocityUtils.generateService(tableInfo, actualOutputDir);
            VelocityUtils.generateServiceImpl(tableInfo, actualOutputDir);
            VelocityUtils.generateController(tableInfo, actualOutputDir);
            return true;
        } catch (Exception e) {
            log.error("代码生成异常 for table: {}", tableInfo.getTableName(), e);
            throw new ServiceException("代码生成异常: " + e.getMessage());
        }
    }

    private TableInfo convertGenTableToTableInfo(GenTable genTable) {
        if (genTable == null) {
            return null;
        }
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(genTable.getTableName());
        tableInfo.setTableComment(genTable.getTableComment());
        tableInfo.setPackageName(genTable.getPackageName());
        tableInfo.setModuleName(genTable.getModuleName());
        tableInfo.setAuthor(genTable.getFunctionAuthor());
        tableInfo.setClassName(genTable.getClassName());
        tableInfo.setClassNameLower(StringUtils.uncapitalize(genTable.getClassName()));

        // 设置表类型
        if (StringUtils.isNotBlank(genTable.getTableType())) {
            try {
                tableInfo.setTableType(TableType.valueOf(genTable.getTableType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid table type: {} for table: {}", genTable.getTableType(), genTable.getTableName());
                // 默认为单表
                tableInfo.setTableType(TableType.SINGLE);
            }
        } else {
            //默认为单表
            tableInfo.setTableType(TableType.SINGLE);
        }

        // 主子表相关字段 (从 GenTable 中获取原始值)
        tableInfo.setSubTableName(genTable.getSubTableName());
        tableInfo.setSubTableFkName(genTable.getSubTableFkName());

        // 如果是主子表，并且子表名不为空，则进行相应转换
        if (TableType.MASTER_CHILD.equals(tableInfo.getTableType())
                && StringUtils.isNotBlank(genTable.getSubTableName())) {
            tableInfo.setSubClassName(GenUtils.convertToCamelCase(genTable.getSubTableName()));
            tableInfo.setSubClassNameLower(StringUtils.uncapitalize(tableInfo.getSubClassName()));
            if (StringUtils.isNotBlank(genTable.getSubTableFkName())) {
                tableInfo.setSubTableFkNameCapitalized(GenUtils.convertToCamelCase(genTable.getSubTableFkName()));
            }
            // 通常子表列表字段名是子类名首字母小写 + List，例如: sysUserList
            tableInfo.setSubTableListName(tableInfo.getSubClassNameLower() + "List");
        }

        // 树形表相关字段 (从 GenTable 中获取原始值)
        tableInfo.setTreeCode(genTable.getTreeCode());
        tableInfo.setTreeParentCode(genTable.getTreeParentCode());
        tableInfo.setTreeName(genTable.getTreeName());

        // 如果是树形表，则进行相应转换
        if (TableType.TREE.equals(tableInfo.getTableType())) {
            if (StringUtils.isNotBlank(genTable.getTreeCode())) {
                tableInfo.setTreeCodeCapitalized(GenUtils.convertToCamelCase(genTable.getTreeCode()));
            }
            if (StringUtils.isNotBlank(genTable.getTreeParentCode())) {
                tableInfo.setTreeParentCodeCapitalized(GenUtils.convertToCamelCase(genTable.getTreeParentCode()));
            }
            if (StringUtils.isNotBlank(genTable.getTreeName())) {
                tableInfo.setTreeNameCapitalized(GenUtils.convertToCamelCase(genTable.getTreeName()));
            }
        }

        List<GenTableColumn> columns = genTable.getColumns();
        if (columns == null || columns.isEmpty()) {
            // 如果 genTable 对象中没有列信息，尝试从数据库加载
            columns = genTableColumnService.list(new LambdaQueryWrapper<GenTableColumn>()
                    .eq(GenTableColumn::getTableId, genTable.getTableId())
                    .orderByAsc(GenTableColumn::getSort));
        }

        if (columns != null && !columns.isEmpty()) {
            List<TableField> fields = new ArrayList<>();
            for (GenTableColumn column : columns) {
                TableField field = getTableField(column);
                // 其他需要转换的字段...

                // 初始化Java字段名、Java类型等
                GenUtils.initTableField(field);
                fields.add(field);
                if (field.getIsPk()) {
                    tableInfo.setPrimaryKey(field);
                }
            }
            tableInfo.setFields(fields);
        }
        // GenUtils.initTableInfo(tableInfo); // className 和 classNameLower 已经在上面设置了
        return tableInfo;
    }

    @Override
    public Page<GenTable> selectGenTableList(GenTableListRequest request) {
        LambdaQueryWrapper<GenTable> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(request.getTableName()), GenTable::getTableName, request.getTableName())
                .like(StringUtils.isNotBlank(request.getTableComment()), GenTable::getTableComment,
                        request.getTableComment())
                .between(request.getBeginTime() != null && request.getEndTime() != null,
                        GenTable::getCreateTime, request.getBeginTime(), request.getEndTime());
        Page<GenTable> page = new Page<>(request.getPageNum(), request.getPageSize());
        // this.page 是 ServiceImpl 的方法
        return this.page(page, wrapper);
    }

    @Override
    public List<DbTableVO> selectDbTableList(String tableName) {
        // baseMapper 是 ServiceImpl<M, T> 中的 M，即 GenTableMapper
        return baseMapper.selectDbTableList(tableName);
    }

    @Override
    public List<DbTableVO> selectDbTableListExcludeGenTable(String tableName) {
        // TODO: 实现查询物理数据库表列表，并排除已在 gen_table 中存在的表
        // 这个方法也应该由 GenTableMapper 提供
        return baseMapper.selectDbTableListExcludeGenTable(tableName);
    }

    @Override
    public List<Map<String, Object>> selectTableColumnList(String tableName) {
        // 此方法可能是查询物理表的列，也可能是查询 gen_table_column
        // 假设是查询 gen_table_column for a given gen_table.table_name
        GenTable genTable = this.getOne(new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName));
        if (genTable == null) {
            throw new ServiceException("表不存在: " + tableName);
        }
        List<GenTableColumn> columns = genTableColumnService.list(
                new LambdaQueryWrapper<GenTableColumn>().eq(GenTableColumn::getTableId, genTable.getTableId()));
        // GenTableColumn 转换为 Map<String, Object>
        // 假设 GenTableColumn 有此方法
        return columns.stream().map(GenTableColumn::convertToMap).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GenTable selectGenTableById(Long id) {
        // this.getById 是 ServiceImpl 的方法
        GenTable genTable = this.getById(id);
        if (genTable != null) {
            List<GenTableColumn> columns = genTableColumnService.list(
                    new LambdaQueryWrapper<GenTableColumn>()
                            .eq(GenTableColumn::getTableId, id)
                            .orderByAsc(GenTableColumn::getSort));
            genTable.setColumns(columns);
        }
        return genTable;
    }

    @Override
    @Transactional(readOnly = true)
    public GenTable selectGenTableByName(String tableName) {
        GenTable genTable = this.getOne(new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName));
        if (genTable != null) {
            List<GenTableColumn> columns = genTableColumnService.list(
                    new LambdaQueryWrapper<GenTableColumn>()
                            .eq(GenTableColumn::getTableId, genTable.getTableId())
                            .orderByAsc(GenTableColumn::getSort));
            genTable.setColumns(columns);
        }
        return genTable;
    }

    @Override
    @Transactional
    public boolean importTable(ImportTableRequest request) {
        List<String> tableNames = request.getTableNames();
        if (tableNames == null || tableNames.isEmpty()) {
            throw new ServiceException("要导入的表名列表不能为空");
        }

        for (String tableName : tableNames) {
            // 检查是否已导入
            GenTable existingTable = this
                    .getOne(new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName));
            if (existingTable != null) {
                log.warn("表 {} 已存在，跳过导入。如需更新请使用同步功能。", tableName);
                continue; // 或者根据策略决定是否覆盖或更新
            }

            DbTableVO dbTableVO = baseMapper.selectDbTableByName(tableName);
            if (dbTableVO == null) {
                log.warn("物理表 {} 不存在，跳过导入。", tableName);
                continue;
            }

            List<GenTableColumn> dbColumns = baseMapper.selectDbTableColumnsByName(tableName);
            if (dbColumns == null || dbColumns.isEmpty()) {
                log.warn("物理表 {} 没有列信息，跳过导入。", tableName);
                continue;
            }

            GenTable genTable = new GenTable();
            genTable.setTableName(tableName);
            genTable.setTableComment(dbTableVO.getTableComment());
            // 表名转类名
            genTable.setClassName(GenUtils.convertToCamelCase(tableName));
            // 从请求获取或设默认
            genTable.setPackageName(request.getPackageName());
            // 从请求获取或设默认
            genTable.setModuleName(request.getModuleName());
            // 通常是表名小驼峰
            genTable.setBusinessName(GenUtils.toCamelCase(tableName));
            // 可以用表注释作为功能名
            genTable.setFunctionName(dbTableVO.getTableComment());
            // 默认作者或从配置取
            genTable.setFunctionAuthor("auto");
            genTable.setTplCategory(request.getTplCategory() != null ? request.getTplCategory() : "crud");
            // 默认为单表，导入后可修改
            genTable.setTableType(TableType.SINGLE.name());
            // genType 和 genPath 暂时使用默认或留空，让用户后续配置
            // 默认zip
            genTable.setGenType("0");
            // genTable.setGenPath("/");

            // 保存 GenTable 以获取 tableId
            // this.save 是 ServiceImpl 的方法
            this.save(genTable);

            List<GenTableColumn> columnsToSave = new ArrayList<>();
            for (GenTableColumn dbColumn : dbColumns) {
                GenTableColumn column = new GenTableColumn();
                column.setTableId(genTable.getTableId());
                column.setColumnName(dbColumn.getColumnName());
                column.setColumnComment(dbColumn.getColumnComment());
                column.setColumnType(dbColumn.getColumnType());
                // isPk, isIncrement 理论上应该从selectDbTableColumnsByName直接获取，这里假设dbColumn已经填充了这些信息
                column.setIsPk(dbColumn.isPk() ? "1" : "0");
                column.setIsIncrement(dbColumn.isIncrement() ? "1" : "0");
                // 根据数据库类型和约束判断是否必填，这里简单处理，实际可能需要更复杂的逻辑或依赖数据库元数据
                // column.setIsRequired(dbColumn.getIsNullable() != null &&
                // dbColumn.getIsNullable().equals("NO") ? "1" : "0");
                // 暂时根据dbColumn中的值，如果它没有isRequired，则按非必填处理
                column.setIsRequired(dbColumn.isRequired() ? "1" : "0");

                // 默认所有字段都可用于增删改查和列表展示，用户后续可编辑
                column.setIsInsert("1");
                column.setIsEdit("1");
                column.setIsList("1");
                column.setIsQuery("1");
                // 默认查询方式为等于
                column.setQueryType("EQ");

                // 使用 GenUtils 初始化 JavaType 和 JavaField
                TableField tempField = new TableField();
                tempField.setColumnName(column.getColumnName());
                tempField.setColumnType(column.getColumnType());
                // 这个方法会设置 javaField 和 javaType
                GenUtils.initTableField(tempField);
                column.setJavaType(tempField.getJavaType());
                column.setJavaField(tempField.getJavaField());

                // htmlType 可以根据字段类型初步判断，或留空让用户配置
                // column.setHtmlType("input");
                // 依赖 selectDbTableColumnsByName 返回 sort
                column.setSort(dbColumn.getSort() != null ? dbColumn.getSort() : 0);

                columnsToSave.add(column);
            }
            if (!columnsToSave.isEmpty()) {
                genTableColumnService.saveBatch(columnsToSave);
            }
            log.info("表 {} 导入成功。", tableName);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateGenTable(GenTableRequest request) {
        GenTable genTable = new GenTable();
        // BeanUtils.copyProperties(request, genTable); // 使用 MapStruct 或手动映射更安全
        genTable.setTableId(request.getTableId());
        genTable.setTableName(request.getTableName());
        genTable.setTableComment(request.getTableComment());
        genTable.setClassName(request.getClassName());
        genTable.setFunctionAuthor(request.getFunctionAuthor());
        genTable.setPackageName(request.getPackageName());
        genTable.setModuleName(request.getModuleName());
        genTable.setBusinessName(request.getBusinessName());
        genTable.setFunctionName(request.getFunctionName());
        genTable.setRemark(request.getRemark());
        genTable.setOptions(request.getOptions());
        // 设置表类型和相关字段
        genTable.setTableType(StringUtils.isNotBlank(request.getTableType()) ? request.getTableType().toUpperCase()
                : TableType.SINGLE.name());
        genTable.setSubTableName(request.getSubTableName());
        genTable.setSubTableFkName(request.getSubTableFkName());
        genTable.setTreeCode(request.getTreeCode());
        genTable.setTreeParentCode(request.getTreeParentCode());
        genTable.setTreeName(request.getTreeName());

        // this.updateById 是 ServiceImpl 的方法
        boolean result = this.updateById(genTable);
        if (result) {
            // 先删除旧的列
            genTableColumnService.deleteGenTableColumnByTableId(genTable.getTableId());
            // 保存新的列信息
            if (request.getColumns() != null && !request.getColumns().isEmpty()) {
                List<GenTableColumn> columnsToSave = request.getColumns();
                for (GenTableColumn column : columnsToSave) {
                    // 确保每个列都关联到正确的表ID
                    column.setTableId(genTable.getTableId());
                }
                genTableColumnService.saveBatch(columnsToSave);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public boolean deleteGenTable(String[] tableNames) {
        if (tableNames == null || tableNames.length == 0) {
            return false;
        }
        for (String tableName : tableNames) {
            // 这个方法内部已经加载了列信息，但这里我们只需要ID
            GenTable genTable = genTableQueryService.selectGenTableByName(tableName);
            if (genTable != null) {
                // 先删除主表记录
                this.removeById(genTable.getTableId());
                // 然后删除关联的列信息
                genTableColumnService.deleteGenTableColumnByTableId(genTable.getTableId());
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteGenTableByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        // 先批量删除主表记录
        boolean result = this.removeByIds(Arrays.asList(ids));
        if (result) {
            // 然后批量删除所有关联的列信息
            genTableColumnService.deleteGenTableColumnByTableIds(ids);
        }
        return result;
    }

    @Override
    public List<PreviewCodeVO> previewCode(String tableName) {
        GenTable genTable = genTableQueryService.selectGenTableByName(tableName);
        if (genTable == null) {
            throw new ServiceException("表不存在: " + tableName);
        }
        TableInfo tableInfo = convertGenTableToTableInfo(genTable);
        if (tableInfo == null) {
            throw new ServiceException("转换表信息失败: " + tableName);
        }

        VelocityUtils.initVelocity(); // 初始化应在应用启动时进行一次

        List<PreviewCodeVO> previewList = new ArrayList<>();
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);

        // 实体
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + ".java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "entity.java.vm"), context)));
        // Mapper
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "Mapper.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "mapper.java.vm"), context)));
        // Mapper XML
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "Mapper.xml",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "mapper.xml.vm"), context)));
        // Service
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "Service.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "service.java.vm"), context)));
        // ServiceImpl
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "ServiceImpl.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "serviceImpl.java.vm"), context)));
        // Controller
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "Controller.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "controller.java.vm"), context)));
        // Request classes
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "AddRequest.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "request/add.java.vm"), context)));
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "UpdateRequest.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "request/update.java.vm"), context)));
        previewList.add(new PreviewCodeVO(tableInfo.getClassName() + "ListRequest.java",
                VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "request/list.java.vm"), context)));

        return previewList;
    }

    @Override
    public byte[] downloadCode(String tableName) throws IOException {
        GenTable genTable = genTableQueryService.selectGenTableByName(tableName);
        if (genTable == null) {
            throw new ServiceException("表不存在: " + tableName);
        }
        TableInfo tableInfo = convertGenTableToTableInfo(genTable);
        if (tableInfo == null) {
            throw new ServiceException("转换表信息失败: " + tableName);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        // rootPath for zip
        generateCodeForZip(tableInfo, zip, "");
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    private void generateCodeForZip(TableInfo tableInfo, ZipOutputStream zip, String rootPath) throws IOException {
        // 应该在应用启动时初始化
        VelocityUtils.initVelocity();
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        // src/main/java/com/example/...
        String packagePath = VelocityUtils.getPackagePath(tableInfo.getPackageName());
        String moduleJavaPath = packagePath + "/" + tableInfo.getModuleName() + "/";
        // 假设资源路径
        String resourcesPath = "src/main/resources/";

        // Entity
        String entityContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "entity.java.vm"),
                context);
        zip.putNextEntry(
                new ZipEntry(rootPath + moduleJavaPath + "model/entity/" + tableInfo.getClassName() + ".java"));
        IOUtils.write(entityContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        // Request Classes
        String addReqContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "request/add.java.vm"),
                context);
        zip.putNextEntry(new ZipEntry(rootPath + moduleJavaPath + "model/request/" + tableInfo.getClassNameLower() + "/"
                + tableInfo.getClassName() + "AddRequest.java"));
        IOUtils.write(addReqContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        String updateReqContent = VelocityUtils.render(
                VelocityUtils.getTemplatePath(tableInfo, "request/update.java.vm"),
                context);
        zip.putNextEntry(new ZipEntry(rootPath + moduleJavaPath + "model/request/" + tableInfo.getClassNameLower() + "/"
                + tableInfo.getClassName() + "UpdateRequest.java"));
        IOUtils.write(updateReqContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        String listReqContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "request/list.java.vm"),
                context);
        zip.putNextEntry(new ZipEntry(rootPath + moduleJavaPath + "model/request/" + tableInfo.getClassNameLower() + "/"
                + tableInfo.getClassName() + "ListRequest.java"));
        IOUtils.write(listReqContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        // Mapper
        String mapperContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "mapper.java.vm"),
                context);
        zip.putNextEntry(
                new ZipEntry(rootPath + moduleJavaPath + "mapper/" + tableInfo.getClassName() + "Mapper.java"));
        IOUtils.write(mapperContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        // Mapper XML
        String mapperXmlContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "mapper.xml.vm"),
                context);
        zip.putNextEntry(new ZipEntry(rootPath + resourcesPath + "mapper/" + tableInfo.getModuleName() + "/"
                + tableInfo.getClassName() + "Mapper.xml"));
        IOUtils.write(mapperXmlContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        // Service
        String serviceContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "service.java.vm"),
                context);
        zip.putNextEntry(
                new ZipEntry(rootPath + moduleJavaPath + "service/" + tableInfo.getClassName() + "Service.java"));
        IOUtils.write(serviceContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        // ServiceImpl
        String serviceImplContent = VelocityUtils.render(
                VelocityUtils.getTemplatePath(tableInfo, "serviceImpl.java.vm"),
                context);
        zip.putNextEntry(
                new ZipEntry(
                        rootPath + moduleJavaPath + "service/impl/" + tableInfo.getClassName() + "ServiceImpl.java"));
        IOUtils.write(serviceImplContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();

        // Controller
        String controllerContent = VelocityUtils.render(VelocityUtils.getTemplatePath(tableInfo, "controller.java.vm"),
                context);
        zip.putNextEntry(
                new ZipEntry(rootPath + moduleJavaPath + "controller/" + tableInfo.getClassName() + "Controller.java"));
        IOUtils.write(controllerContent, zip, StandardCharsets.UTF_8);
        zip.closeEntry();
    }

    @Override
    public byte[] batchGenerateCode(BatchGenCodeRequest request) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        if (request.getTableIds() != null && !request.getTableIds().isEmpty()) {
            for (Long tableId : request.getTableIds()) {
                GenTable genTable = genTableQueryService.selectGenTableById(tableId);
                if (genTable != null) {
                    TableInfo tableInfo = convertGenTableToTableInfo(genTable);
                    if (tableInfo != null) {
                        generateCodeForZip(tableInfo, zip, tableInfo.getClassName() + "/");
                    } else {
                        log.warn("转换表信息失败，跳过表ID: {}", tableId);
                    }
                } else {
                    log.warn("表不存在，跳过表ID: {}", tableId);
                }
            }
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    @Transactional // 通常生成到路径不需要事务，但如果包含数据库操作则需要
    public boolean genToPath(String tableName) {
        GenTable genTable = genTableQueryService.selectGenTableByName(tableName);
        if (genTable == null) {
            throw new ServiceException("表不存在: " + tableName);
        }
        TableInfo tableInfo = convertGenTableToTableInfo(genTable);
        if (tableInfo == null) {
            throw new ServiceException("转换表信息失败: " + tableName);
        }

        // 从 GenTable 获取生成路径
        String outputPath = genTable.getGenPath();
        if (StringUtils.isEmpty(outputPath)) {
            // 如果没有配置特定路径，可以设置一个默认路径，或者抛出异常要求配置
            throw new ServiceException("请为表 " + tableName + " 配置生成路径 (genPath)");
        }

        try {
            return generateCodeLogic(tableInfo, outputPath);
        } catch (IOException e) {
            log.error("生成代码到路径 {} 失败 for table {}", outputPath, tableName, e);
            throw new ServiceException("生成代码到路径失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean syncDb(String tableName) {
        // 已包含 columns
        GenTable genTable = genTableQueryService.selectGenTableByName(tableName);
        if (genTable == null) {
            throw new ServiceException("表 " + tableName + " 未在代码生成模块中配置，无法同步。");
        }

        // 1. 获取物理表信息
        DbTableVO dbTableVO = baseMapper.selectDbTableByName(tableName);
        if (dbTableVO == null) {
            // 物理表不存在了，可以选择删除 gen_table 中的记录或报错
            log.warn("物理表 {} 不存在，但代码生成配置中存在。考虑删除配置或重建物理表。", tableName);
            throw new ServiceException("物理表 " + tableName + " 不存在，无法同步。");
        }

        List<GenTableColumn> dbColumns = baseMapper.selectDbTableColumnsByName(tableName);
        // 允许空列列表，但不允许为null
        if (dbColumns == null) {
            dbColumns = new ArrayList<>();
        }

        // 2. 更新 GenTable 的基本信息 (如表注释)
        boolean tableInfoChanged = false;
        if (!StringUtils.equals(genTable.getTableComment(), dbTableVO.getTableComment())) {
            genTable.setTableComment(dbTableVO.getTableComment());
            tableInfoChanged = true;
        }
        // 如果 className 等是根据 tableName 生成的，通常不需要在同步时改变，除非表名本身变了（这种情况处理会更复杂）
        // 如果需要，可以比较 genTable.getClassName() 和
        // GenUtils.convertToCamelCase(dbTableVO.getTableName()) 等

        if (tableInfoChanged) {
            this.updateById(genTable);
        }

        // 3. 对比并更新列信息
        List<GenTableColumn> existingGenColumns = genTable.getColumns();
        if (existingGenColumns == null) {
            existingGenColumns = new ArrayList<>();
        }

        Map<String, GenTableColumn> dbColumnsMap = dbColumns.stream()
                // (c1,c2)->c1 to handle
                .collect(Collectors.toMap(GenTableColumn::getColumnName, c -> c, (c1, c2) -> c1));
        // potential duplicates if
        // any
        Map<String, GenTableColumn> existingGenColumnsMap = existingGenColumns.stream()
                .collect(Collectors.toMap(GenTableColumn::getColumnName, c -> c));

        List<GenTableColumn> columnsToAdd = new ArrayList<>();
        List<GenTableColumn> columnsToUpdate = new ArrayList<>();
        List<Long> columnIdsToDelete = new ArrayList<>();

        // 检查物理表中的列
        for (GenTableColumn dbCol : dbColumns) {
            GenTableColumn existingCol = existingGenColumnsMap.get(dbCol.getColumnName());
            // 物理表有，gen_table_column 没有 -> 新增
            if (existingCol == null) {
                GenTableColumn newCol = new GenTableColumn();
                newCol.setTableId(genTable.getTableId());
                newCol.setColumnName(dbCol.getColumnName());
                newCol.setColumnComment(dbCol.getColumnComment());
                newCol.setColumnType(dbCol.getColumnType());
                newCol.setIsPk(dbCol.isPk() ? "1" : "0");
                newCol.setIsIncrement(dbCol.isIncrement() ? "1" : "0");
                newCol.setIsRequired(dbCol.isRequired() ? "1" : "0");
                newCol.setSort(dbCol.getSort() != null ? dbCol.getSort() : 0);

                // 初始化 JavaType, JavaField, 和一些默认的生成属性
                TableField tempField = new TableField();
                tempField.setColumnName(newCol.getColumnName());
                tempField.setColumnType(newCol.getColumnType());
                GenUtils.initTableField(tempField);
                newCol.setJavaType(tempField.getJavaType());
                newCol.setJavaField(tempField.getJavaField());
                newCol.setIsInsert("1");
                newCol.setIsEdit("1");
                newCol.setIsList("1");
                newCol.setIsQuery("1");
                newCol.setQueryType("EQ");
                // newCol.setHtmlType("input"); // 可以留空让用户配置
                columnsToAdd.add(newCol);
            } else { // 两边都有，检查是否需要更新
                boolean colChanged = false;
                if (!StringUtils.equals(existingCol.getColumnComment(), dbCol.getColumnComment())) {
                    existingCol.setColumnComment(dbCol.getColumnComment());
                    colChanged = true;
                }
                if (!StringUtils.equals(existingCol.getColumnType(), dbCol.getColumnType())) {
                    existingCol.setColumnType(dbCol.getColumnType());
                    // 列类型变化，Java类型和字段名可能也需要重新生成
                    TableField tempField = new TableField();
                    tempField.setColumnName(existingCol.getColumnName());
                    tempField.setColumnType(existingCol.getColumnType());
                    GenUtils.initTableField(tempField);
                    existingCol.setJavaType(tempField.getJavaType());
                    existingCol.setJavaField(tempField.getJavaField());
                    colChanged = true;
                }
                String dbIsPk = dbCol.isPk() ? "1" : "0";
                if (!StringUtils.equals(existingCol.getIsPk(), dbIsPk)) {
                    existingCol.setIsPk(dbIsPk);
                    colChanged = true;
                }
                String dbIsIncrement = dbCol.isIncrement() ? "1" : "0";
                if (!StringUtils.equals(existingCol.getIsIncrement(), dbIsIncrement)) {
                    existingCol.setIsIncrement(dbIsIncrement);
                    colChanged = true;
                }
                String dbIsRequired = dbCol.isRequired() ? "1" : "0";
                if (!StringUtils.equals(existingCol.getIsRequired(), dbIsRequired)) {
                    existingCol.setIsRequired(dbIsRequired);
                    colChanged = true;
                }
                if (dbCol.getSort() != null && !dbCol.getSort().equals(existingCol.getSort())) {
                    existingCol.setSort(dbCol.getSort());
                    colChanged = true;
                }
                // 注意：用户在UI上修改的 isInsert, isEdit, isList, isQuery, queryType, htmlType, dictType
                // 不应该被物理表结构同步覆盖
                // 这些是代码生成的配置，而不是数据库本身的属性。除非有特定需求。
                if (colChanged) {
                    columnsToUpdate.add(existingCol);
                }
            }
        }

        // 检查 gen_table_column 中存在，但物理表已不存在的列
        for (GenTableColumn existingCol : existingGenColumns) {
            if (!dbColumnsMap.containsKey(existingCol.getColumnName())) {
                columnIdsToDelete.add(existingCol.getColumnId());
            }
        }

        if (!columnsToAdd.isEmpty()) {
            genTableColumnService.saveBatch(columnsToAdd);
        }
        if (!columnsToUpdate.isEmpty()) {
            genTableColumnService.updateBatchById(columnsToUpdate);
        }
        if (!columnIdsToDelete.isEmpty()) {
            genTableColumnService.removeByIds(columnIdsToDelete);
        }

        log.info("表 {} 同步数据库完成。新增 {} 列，更新 {} 列，删除 {} 列。",
                tableName, columnsToAdd.size(), columnsToUpdate.size(), columnIdsToDelete.size());
        return true;
    }

    @Override
    @Transactional
    public boolean executeSql(ExecuteSqlRequest request) {
        String sql = request.getSqlContent();
        if (StringUtils.isBlank(sql)) {
            throw new ServiceException("SQL语句不能为空");
        }
        // 移除注释并转换为大写，方便检查
        // 移除行注释
        String processedSql = sql.replaceAll("--.*", "")
                // 移除块注释 (非贪婪)
                .replaceAll("/\\*.*?\\*/", "")
                .trim().toUpperCase();

        if (!processedSql.startsWith("CREATE TABLE")) {
            throw new ServiceException("只允许执行CREATE TABLE语句，以确保安全。");
        }

        try {
            // 调用mapper执行
            baseMapper.executeDDL(sql);
            log.info("成功执行SQL: {}", sql);
            return true;
        } catch (Exception e) {
            log.error("执行SQL失败: {}\n错误: {}", sql, e.getMessage());
            throw new ServiceException("执行SQL失败: " + e.getMessage());
        }
    }
}
