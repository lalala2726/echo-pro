package cn.zhangchuangla.generator.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.mapper.GenTableMapper;
import cn.zhangchuangla.generator.mapper.GenTableColumnMapper;
import com.alibaba.fastjson.JSON; // 新增：导入FastJSON
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板工具类
 *
 * @author Chuang
 */
public class VelocityUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "main/java";

    /**
     * 默认上级菜单，系统工具
     */
    private static final String DEFAULT_PARENT_MENU_ID = "3";

    /**
     * 获取Java子项DTO模板路径
     * @return 模板路径字符串
     */
    public static String getJavaDtoSubItemTemplate() {
        // 返回Java子项DTO模板的文件路径
        return "vm/java/dto/sub-item.java.vm";
    }

    private static final Logger log = LoggerFactory.getLogger(VelocityUtils.class);

    /**
     * 设置模板变量信息
     *
     * @param genTable 表信息
     * @param genTableMapper GenTable数据层
     * @param genTableColumnMapper GenTableColumn数据层
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable, GenTableMapper genTableMapper, GenTableColumnMapper genTableColumnMapper) { // 修改：添加Mapper参数
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String functionName = genTable.getFunctionName();
        String tplCategory = genTable.getTplCategory();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", tplCategory);
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("tableComment", genTable.getTableComment());
        velocityContext.put("functionName", StrUtil.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StrUtil.lowerFirst(genTable.getClassName()));
        velocityContext.put("classNameLower", StrUtil.lowerFirst(genTable.getClassName()));
        velocityContext.put("moduleName", moduleName);
        velocityContext.put("BusinessName", StrUtil.upperFirst(businessName));
        velocityContext.put("businessName", businessName);
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", DateUtil.format(new Date(), "yyyy-MM-dd"));
        velocityContext.put("pkColumn", getPkColumn(genTable.getColumns()));
        velocityContext.put("importList", getImportList(genTable.getColumns()));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);
        velocityContext.put("dicts", getDicts(genTable.getColumns()));
        velocityContext.put("parentMenuId", DEFAULT_PARENT_MENU_ID);

        // 添加主键信息
        GenTableColumn pkColumn = getPkColumn(genTable.getColumns());
        velocityContext.put("primaryKey", pkColumn);

        // 树表相关变量
        if (Constants.Generator.TREE.equals(tplCategory)) {
            setTreeTemplateContext(velocityContext, genTable);
        }

        // 主子表相关变量
        if (Constants.Generator.SUB.equals(tplCategory)) {
            // 修改：传递Mapper到setSubTemplateContext
            setSubTemplateContext(velocityContext, genTable, genTableMapper, genTableColumnMapper);
        }

        return velocityContext;
    }

    /**
     * 设置树表模板上下文
     */
    private static void setTreeTemplateContext(VelocityContext context, GenTable genTable) {
        List<GenTableColumn> columns = genTable.getColumns();

        // 优先使用用户配置的字段
        String treeCode = genTable.getTreeCode();
        String treeParentCode = genTable.getTreeParentCode();
        String treeName = genTable.getTreeName();

        // 如果没有配置，则自动推断
        if (StrUtil.isBlank(treeCode) || StrUtil.isBlank(treeParentCode) || StrUtil.isBlank(treeName)) {
            GenTableColumn treeCodeColumn = null;
            GenTableColumn treeParentCodeColumn = null;
            GenTableColumn treeNameColumn = null;

            for (GenTableColumn column : columns) {
                String columnName = column.getColumnName().toLowerCase();

                // 如果没有配置树编码字段，使用主键
                if (StrUtil.isBlank(treeCode) && Constants.Generator.YES.equals(column.getIsPk())) {
                    treeCodeColumn = column;
                    treeCode = column.getColumnName();
                }

                // 如果没有配置父编码字段，查找包含parent和id的字段
                if (StrUtil.isBlank(treeParentCode) && columnName.contains("parent") && columnName.contains("id")) {
                    treeParentCodeColumn = column;
                    treeParentCode = column.getColumnName();
                }

                // 如果没有配置名称字段，查找包含name或title的字段
                if (StrUtil.isBlank(treeName) && (columnName.contains("name") || columnName.contains("title"))) {
                    treeNameColumn = column;
                    treeName = column.getColumnName();
                }
            }

            // 设置默认值
            if (StrUtil.isBlank(treeCode) && !columns.isEmpty()) {
                treeCodeColumn = getPkColumn(columns);
                treeCode = treeCodeColumn != null ? treeCodeColumn.getColumnName() : "id";
            }
            if (StrUtil.isBlank(treeName) && columns.size() > 1) {
                treeNameColumn = columns.get(1); // 取第二个字段作为名称字段
                treeName = treeNameColumn.getColumnName();
            }
        }

        // 根据字段名查找对应的GenTableColumn对象，用于获取Java字段名
        GenTableColumn treeCodeColumn = findColumnByName(columns, treeCode);
        GenTableColumn treeParentCodeColumn = findColumnByName(columns, treeParentCode);
        GenTableColumn treeNameColumn = findColumnByName(columns, treeName);

        // 设置模板变量
        context.put("treeCode", treeCodeColumn != null ? treeCodeColumn.getJavaField() : toCamelCase(treeCode));
        context.put("treeParentCode", treeParentCodeColumn != null ? treeParentCodeColumn.getJavaField() : toCamelCase(treeParentCode));
        context.put("treeName", treeNameColumn != null ? treeNameColumn.getJavaField() : toCamelCase(treeName));
        context.put("TreeCode", treeCodeColumn != null ? StrUtil.upperFirst(treeCodeColumn.getJavaField()) : StrUtil.upperFirst(toCamelCase(treeCode)));
        context.put("TreeParentCode", treeParentCodeColumn != null ? StrUtil.upperFirst(treeParentCodeColumn.getJavaField()) : StrUtil.upperFirst(toCamelCase(treeParentCode)));
        context.put("TreeName", treeNameColumn != null ? StrUtil.upperFirst(treeNameColumn.getJavaField()) : StrUtil.upperFirst(toCamelCase(treeName)));
    }

    /**
     * 设置主子表模板上下文 (setSubTemplateContext)
     *
     * @param context Velocity上下文
     * @param genTable 主表信息
     * @param genTableMapper GenTable数据层 (用于查询子表GenTable)
     * @param genTableColumnMapper GenTableColumn数据层 (用于查询子表字段)
     */
    private static void setSubTemplateContext(VelocityContext context, GenTable genTable, GenTableMapper genTableMapper, GenTableColumnMapper genTableColumnMapper) {
        // 获取主表配置的子表名
        String subTableNameConfig = genTable.getSubTableName();
        // 获取主表配置的子表外键名
        String subTableFkNameConfig = genTable.getSubTableFkName();

        if (StrUtil.isBlank(subTableNameConfig)) {
            log.warn("主表 {} ({}) 未配置子表名，跳过子表相关变量设置。", genTable.getTableName(), genTable.getTableComment());
            return;
        }

        // 1. 查询子表GenTable信息
        // 注意: GenTableMapper 通常通过依赖注入使用，此处作为参数传入。
        // 如果GenTableMapper为null，则无法继续，实际应用中应确保其可用。
        if (genTableMapper == null || genTableColumnMapper == null) {
            log.error("GenTableMapper 或 GenTableColumnMapper 未提供，无法处理子表信息。");
            // 在此可以决定是抛出异常还是静默失败
            return;
        }
        GenTable subGenTable = genTableMapper.selectGenTableByName(subTableNameConfig);

        if (subGenTable == null) {
            log.warn("根据子表名 '{}' 未找到对应的GenTable记录，跳过子表模板变量设置。", subTableNameConfig);
            return;
        }

        // 2. 查询子表字段信息
        List<GenTableColumn> subColumns = genTableColumnMapper.selectGenTableColumnListByTableId(subGenTable.getTableId());
        if (CollUtil.isEmpty(subColumns)) {
            log.warn("子表 '{}' ({}) 未配置字段信息或查询结果为空。", subGenTable.getTableName(), subGenTable.getTableComment());
            // 即使没有字段，也可能需要生成DTO，所以不直接返回
        }

        // 3. 准备子表相关变量
        String subClassName = subGenTable.getClassName(); // GenTable实体中应已包含转换后的类名
        String subClassNameDto = subClassName + "Dto";
        String subClassNameLowerList = StrUtil.lowerFirst(subClassName) + "List"; // 例如: orderItemList
        String subTableComment = StrUtil.isNotBlank(subGenTable.getFunctionComment()) ? subGenTable.getFunctionComment() : subGenTable.getTableComment();
        String subTableFkName = StrUtil.isNotBlank(subTableFkNameConfig) ? subTableFkNameConfig : (getPkColumn(genTable.getColumns()) != null ? getPkColumn(genTable.getColumns()).getColumnName() : "id"); // 子表外键字段名，默认为主表主键

        // 4. 获取子表DTO所需的导入包
        HashSet<String> subDtoImports = new HashSet<>();
        if (subColumns != null) {
            for (GenTableColumn column : subColumns) {
                // 根据Java类型添加特定导入 (不需要MyBatis Plus等实体类特有的导入)
                // sub-item.java.vm 模板会自行处理 jakarta.validation.constraints.* 的导入
                // io.swagger.v3.oas.annotations.media.Schema 也会在模板中处理
                String javaType = column.getJavaType();
                if ("Date".equals(javaType)) {
                    subDtoImports.add("java.util.Date");
                } else if ("BigDecimal".equals(javaType)) {
                    subDtoImports.add("java.math.BigDecimal");
                } else if ("LocalDateTime".equals(javaType)) {
                    subDtoImports.add("java.time.LocalDateTime");
                } else if ("LocalDate".equals(javaType)) {
                    subDtoImports.add("java.time.LocalDate");
                } else if ("LocalTime".equals(javaType)) {
                    subDtoImports.add("java.time.LocalTime");
                }
                // 其他类型如List, Set等通常在模板中直接使用泛型，不需要在此显式导入
                // Lombok @Data等也不需要在此导入
            }
        }

        // 5. 将变量放入VelocityContext
        context.put("subTableGenTable", subGenTable);         // 子表GenTable对象，用于getFileName等场景
        context.put("subClassName", subClassName);             // 子表类名 (例如: OrderItem)
        context.put("subClassNameDto", subClassNameDto);       // 子表DTO类名 (例如: OrderItemDto)
        context.put("subClassNameLowerList", subClassNameLowerList); // 子表列表在主表DTO中的字段名 (例如: orderItemList)
        context.put("subTableComment", subTableComment);       // 子表注释
        context.put("subColumns", subColumns);                 // 子表字段列表
        context.put("subDtoImports", subDtoImports);           // 子表DTO所需的导入包
        context.put("subTableFkName", subTableFkName);         // 子表外键字段数据库名
        context.put("subTableFkJavaField", toCamelCase(subTableFkName)); // 子表外键字段Java名
        context.put("SubTableFkJavaField", StrUtil.upperFirst(toCamelCase(subTableFkName))); // 子表外键字段Java名首字母大写

        // 6. 为Vue模板准备子表元数据JSON (供前端动态生成表单和规则使用)
        Map<String, Object> subTableDataForJson = new HashMap<>();
        subTableDataForJson.put("functionName", subGenTable.getFunctionName()); // 子表功能名 (例如: "订单项")

        List<Map<String, Object>> simplifiedSubColumns = new ArrayList<>();
        if (subColumns != null) {
            for (GenTableColumn column : subColumns) {
                Map<String, Object> simplifiedColumn = new HashMap<>();
                simplifiedColumn.put("javaField", column.getJavaField());
                simplifiedColumn.put("columnComment", column.getColumnComment());
                simplifiedColumn.put("isRequired", column.getIsRequired()); // 必需 (String '1' or '0')
                simplifiedColumn.put("isInsert", column.getIsInsert());     // 插入时显示 (String '1' or '0')
                simplifiedColumn.put("isEdit", column.getIsEdit());         // 编辑时显示 (String '1' or '0')
                // simplifiedColumn.put("htmlType", column.getHtmlType()); // 可选: 如果前端需要根据HTML类型做更多判断
                simplifiedSubColumns.add(simplifiedColumn);
            }
        }
        subTableDataForJson.put("columns", simplifiedSubColumns); // 简化后的子表列信息

        // 使用FastJSON序列化为字符串
        String subTableJsonString = JSON.toJSONString(subTableDataForJson);
        context.put("subTableJson", subTableJsonString); // 将JSON字符串放入Velocity上下文

        // 为了getFileName能够获取到subClassNameDto，将其存入genTable的params中
        // params是一个Map<String, Object>，通常用于在生成器各阶段传递临时数据
        if (genTable.getParams() == null) {
            genTable.setParams(new HashMap<>());
        }
        genTable.getParams().put("subClassNameDto", subClassNameDto); // 存储子类DTO名
        // 也存储子表的packageName和moduleName，如果它们可能与主表不同
        genTable.getParams().put("subPackageName", subGenTable.getPackageName());
        genTable.getParams().put("subModuleName", subGenTable.getModuleName());


        // 原有的subTable Map结构可以保留，如果旧模板或其他地方仍在使用
        // 但新的变量更直接，建议模板优先使用新变量
        Map<String, Object> subTableMap = new HashMap<>();
        subTableMap.put("tableName", subGenTable.getTableName());
        subTableMap.put("className", subClassName);
        subTableMap.put("classNameLower", StrUtil.lowerFirst(subClassName));
        subTableMap.put("businessName", subGenTable.getBusinessName()); // 使用子表自己的业务名
        subTableMap.put("functionName", subGenTable.getFunctionName());
        subTableMap.put("fkName", subTableFkName);
        subTableMap.put("fkJavaField", toCamelCase(subTableFkName));
        subTableMap.put("columns", subColumns); // 使用真实的子表列
        context.put("subTable", subTableMap); // 旧的subTable变量，填充真实数据
    }

    /**
     * 根据字段名查找对应的GenTableColumn对象
     */
    private static GenTableColumn findColumnByName(List<GenTableColumn> columns, String columnName) {
        if (StrUtil.isBlank(columnName) || columns == null) {
            return null;
        }
        return columns.stream()
                .filter(column -> columnName.equals(column.getColumnName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 将下划线命名转换为驼峰命名
     */
    private static String toCamelCase(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        return StrUtil.toCamelCase(str);
    }

    /**
     * 获取模板信息
     *
     * @param tplCategory 模板类型
     * @return 模板列表
     */
    public static List<String> getTemplateList(String tplCategory) {
        List<String> templates = new ArrayList<>();

        // Java后端模板优先 - Controller在最前面
        // 根据不同的模板类型（CRUD, Tree, Sub）添加相应的Java后端核心模板
        if (Constants.Generator.CRUD.equals(tplCategory)) {
            templates.add("vm/java/controller.java.vm");
            templates.add("vm/java/service.java.vm");
            templates.add("vm/java/serviceImpl.java.vm");
        } else if (Constants.Generator.TREE.equals(tplCategory)) {
            templates.add("vm/java/tree/controller.java.vm");
            templates.add("vm/java/tree/service.java.vm");
            templates.add("vm/java/tree/serviceImpl.java.vm");
        } else if (Constants.Generator.SUB.equals(tplCategory)) {
            templates.add("vm/java/sub/controller.java.vm");
            templates.add("vm/java/sub/service.java.vm");
            templates.add("vm/java/sub/serviceImpl.java.vm");
            // 如果是主子表类型，添加子项DTO模板
            templates.add(getJavaDtoSubItemTemplate()); // 新增：添加子项DTO模板
        }

        // Java基础模板 (所有类型共用)
        templates.add("vm/java/entity.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        templates.add("vm/java/vo.java.vm");
        templates.add("vm/java/list-vo.java.vm");
        templates.add("vm/java/add-request.java.vm");
        templates.add("vm/java/update-request.java.vm");
        templates.add("vm/java/request.java.vm");

        // 前端模板放在最后
        if (Constants.Generator.CRUD.equals(tplCategory)) {
            templates.add("vm/vue/api.ts.vm");
            templates.add("vm/vue/types.ts.vm");
            templates.add("vm/vue/crud/index.vue.vm");
        } else if (Constants.Generator.TREE.equals(tplCategory)) {
            templates.add("vm/vue/api.ts.vm");
            templates.add("vm/vue/types.ts.vm");
            templates.add("vm/vue/tree/index.vue.vm");
        } else if (Constants.Generator.SUB.equals(tplCategory)) {
            templates.add("vm/vue/api.ts.vm");
            templates.add("vm/vue/types.ts.vm");
            templates.add("vm/vue/sub/index.vue.vm");
        }

        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable) {
        // 文件名称
        String fileName = "";
        // 主表包路径
        String mainPackageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String mainJavaPath = PROJECT_PATH + "/" + StrUtil.replace(mainPackageName, ".", "/");
        String mapperXmlPath = "main/resources/mapper/" + moduleName; // 通常主子表mapper在同一模块
        String vuePath = "src/views/" + moduleName + "/" + businessName;
        String apiPath = "src/api/" + moduleName;
        String typesPath = "src/types/" + moduleName;

        // 根据模板路径判断生成文件名
        if (template.contains("entity.java.vm")) {
            fileName = StrUtil.format("{}/model/entity/{}.java", mainJavaPath, className);
        } else if (template.contains("mapper.java.vm")) {
            fileName = StrUtil.format("{}/mapper/{}Mapper.java", mainJavaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StrUtil.format("{}/service/{}Service.java", mainJavaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StrUtil.format("{}/service/impl/{}ServiceImpl.java", mainJavaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = StrUtil.format("{}/controller/{}Controller.java", mainJavaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StrUtil.format("{}/{}Mapper.xml", mapperXmlPath, className);
        } else if (template.contains("list-vo.java.vm")) {
            fileName = StrUtil.format("{}/model/vo/{}/{}ListVo.java", mainJavaPath, businessName, className);
        } else if (template.contains("vo.java.vm")) {
            fileName = StrUtil.format("{}/model/vo/{}/{}Vo.java", mainJavaPath, businessName, className);
        } else if (template.contains("add-request.java.vm")) {
            fileName = StrUtil.format("{}/model/request/{}/{}AddRequest.java", mainJavaPath, businessName, className);
        } else if (template.contains("update-request.java.vm")) {
            fileName = StrUtil.format("{}/model/request/{}/{}UpdateRequest.java", mainJavaPath, businessName, className);
        } else if (template.contains("request.java.vm")) {
            fileName = StrUtil.format("{}/model/request/{}/{}QueryRequest.java", mainJavaPath, businessName, className);
        }
        // 新增：处理子项DTO文件名
        else if (template.equals(getJavaDtoSubItemTemplate())) {
            // 从genTable的params中获取子类DTO名 (在prepareContext中设置)
            String subClassNameDto = (genTable.getParams() != null) ? (String) genTable.getParams().get("subClassNameDto") : null;
            // 子表可能在不同的包或模块，这里假设它和主表在同一个包下，但可以从subGenTable获取实际的包路径
            String subPackageName = (genTable.getParams() != null && genTable.getParams().get("subPackageName") != null) ? (String) genTable.getParams().get("subPackageName") : mainPackageName;
            String subJavaPath = PROJECT_PATH + "/" + StrUtil.replace(subPackageName, ".", "/");

            if (StrUtil.isNotBlank(subClassNameDto)) {
                fileName = StrUtil.format("{}/model/dto/{}.java", subJavaPath, subClassNameDto); // 子项DTO放在model/dto/目录下
            } else {
                // 如果subClassNameDto未设置，记录错误或抛出异常
                log.error("无法生成子项DTO文件名，因为 'subClassNameDto' 未在GenTable的params中设置。模板: {}", template);
                // 可以选择返回一个空文件名或特定错误标记，或者抛出异常
                // fileName = ""; // 或者 throw new IllegalStateException("subClassNameDto not set");
            }
        }
        // 前端文件名处理
        else if (template.contains("api.ts.vm")) {
            fileName = StrUtil.format("{}/{}.ts", apiPath, businessName);
        } else if (template.contains("types.ts.vm")) {
            fileName = StrUtil.format("{}/type{}.ts", typesPath, StrUtil.upperFirst(businessName));
        } else if (template.contains("index.vue.vm")) {
            fileName = StrUtil.format("{}/index.vue", vuePath);
        }

        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return lastIndex > -1 ? packageName.substring(0, lastIndex) : packageName;
    }

    /**
     * 根据列类型获取导入包
     *
     * @param columns 列信息
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(List<GenTableColumn> columns) {
        HashSet<String> importList = new HashSet<>();
        importList.add("com.baomidou.mybatisplus.annotation.TableName");
        importList.add("com.baomidou.mybatisplus.annotation.TableId");
        importList.add("com.baomidou.mybatisplus.annotation.IdType");
        importList.add("lombok.Data");

        // 检查列表是否为空
        if (columns == null || columns.isEmpty()) {
            return importList;
        }

        // 根据列类型添加对应的导入包
        for (GenTableColumn column : columns) {
            String javaType = column.getJavaType();
            if ("Date".equals(javaType)) {
                importList.add("java.util.Date");
            } else if ("BigDecimal".equals(javaType)) {
                importList.add("java.math.BigDecimal");
            } else if ("LocalDateTime".equals(javaType)) {
                importList.add("java.time.LocalDateTime");
            } else if ("LocalDate".equals(javaType)) {
                importList.add("java.time.LocalDate");
            } else if ("LocalTime".equals(javaType)) {
                importList.add("java.time.LocalTime");
            }
        }

        return importList;
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName   模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    public static String getPermissionPrefix(String moduleName, String businessName) {
        return StrUtil.format("{}.{}", moduleName, businessName);
    }

    /**
     * 获取主键列
     *
     * @param columns 表字段列表
     * @return 主键列
     */
    public static GenTableColumn getPkColumn(List<GenTableColumn> columns) {
        // 检查列表是否为空
        if (columns == null || columns.isEmpty()) {
            // 如果列表为空，则创建一个默认的主键列
            GenTableColumn column = new GenTableColumn();
            column.setColumnName("id");
            column.setJavaField("id");
            column.setJavaType("Long");
            return column;
        }

        // 从表字段列表中查找主键列
        return columns.stream()
                .filter(column -> "1".equals(column.getIsPk()))
                .findFirst()
                .orElseGet(() -> {
                    // 如果没有找到主键列，则创建一个默认的主键列
                    GenTableColumn column = new GenTableColumn();
                    column.setColumnName("id");
                    column.setJavaField("id");
                    column.setJavaType("Long");
                    return column;
                });
    }

    /**
     * 获取字典列表
     *
     * @param columns 表字段列表
     * @return 字典列表
     */
    public static List<String> getDicts(List<GenTableColumn> columns) {
        // 检查列表是否为空
        if (columns == null || columns.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有配置了字典类型的列的字典类型
        return columns.stream()
                .map(GenTableColumn::getDictType)
                .filter(StrUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 初始化Velocity引擎
     */
    public static void initVelocity() {
        Properties p = new Properties();
        try {
            // 加载classpath目录下的vm文件
            p.setProperty("resource.loader.file.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.name());
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
