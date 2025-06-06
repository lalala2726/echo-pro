package cn.zhangchuangla.generator.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

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
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable) {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String functionName = genTable.getFunctionName();
        String tplCategory = genTable.getTplCategory();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", tplCategory);
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("tableComment", genTable.getTableComment());
        velocityContext.put("functionName", StringUtils.isNotBlank(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("classNameLower", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", moduleName);
        velocityContext.put("BusinessName", StringUtils.capitalize(businessName));
        velocityContext.put("businessName", businessName);
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
            setSubTemplateContext(velocityContext, genTable);
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
        if (StringUtils.isBlank(treeCode) || StringUtils.isBlank(treeParentCode) || StringUtils.isBlank(treeName)) {
            GenTableColumn treeCodeColumn = null;
            GenTableColumn treeParentCodeColumn = null;
            GenTableColumn treeNameColumn = null;

            for (GenTableColumn column : columns) {
                String columnName = column.getColumnName().toLowerCase();

                // 如果没有配置树编码字段，使用主键
                if (StringUtils.isBlank(treeCode) && Constants.Generator.YES.equals(column.getIsPk())) {
                    treeCodeColumn = column;
                    treeCode = column.getColumnName();
                }

                // 如果没有配置父编码字段，查找包含parent和id的字段
                if (StringUtils.isBlank(treeParentCode) && columnName.contains("parent") && columnName.contains("id")) {
                    treeParentCodeColumn = column;
                    treeParentCode = column.getColumnName();
                }

                // 如果没有配置名称字段，查找包含name或title的字段
                if (StringUtils.isBlank(treeName) && (columnName.contains("name") || columnName.contains("title"))) {
                    treeNameColumn = column;
                    treeName = column.getColumnName();
                }
            }

            // 设置默认值
            if (StringUtils.isBlank(treeCode) && !columns.isEmpty()) {
                treeCodeColumn = getPkColumn(columns);
                treeCode = treeCodeColumn != null ? treeCodeColumn.getColumnName() : "id";
            }
            if (StringUtils.isBlank(treeName) && columns.size() > 1) {
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
        context.put("TreeCode", treeCodeColumn != null ? StringUtils.capitalize(treeCodeColumn.getJavaField()) : StringUtils.capitalize(toCamelCase(treeCode)));
        context.put("TreeParentCode", treeParentCodeColumn != null ? StringUtils.capitalize(treeParentCodeColumn.getJavaField()) : StringUtils.capitalize(toCamelCase(treeParentCode)));
        context.put("TreeName", treeNameColumn != null ? StringUtils.capitalize(treeNameColumn.getJavaField()) : StringUtils.capitalize(toCamelCase(treeName)));
    }

    /**
     * 设置主子表模板上下文
     */
    private static void setSubTemplateContext(VelocityContext context, GenTable genTable) {
        // 使用用户配置的子表信息
        String subTableName = genTable.getSubTableName();
        String subTableFkName = genTable.getSubTableFkName();

        // 如果没有配置，设置默认值
        if (StringUtils.isBlank(subTableName)) {
            subTableName = "sub_" + genTable.getTableName();
        }
        if (StringUtils.isBlank(subTableFkName)) {
            GenTableColumn pkColumn = getPkColumn(genTable.getColumns());
            subTableFkName = pkColumn != null ? pkColumn.getColumnName() : "id";
        }

        // 生成子表类名和业务名称
        String subClassName = genTable.getClassName() + "Detail";
        String subClassNameLower = StringUtils.uncapitalize(subClassName);
        String subBusinessName = genTable.getBusinessName() + "Detail";
        String subFunctionName = genTable.getFunctionName() + "详情";

        // 创建子表对象用于模板
        Map<String, Object> subTable = new HashMap<>();
        subTable.put("tableName", subTableName);
        subTable.put("className", subClassName);
        subTable.put("classNameLower", subClassNameLower);
        subTable.put("businessName", subBusinessName);
        subTable.put("functionName", subFunctionName);
        subTable.put("fkName", subTableFkName);
        subTable.put("fkJavaField", toCamelCase(subTableFkName));

        // 创建子表字段列表（示例字段）
        List<Map<String, Object>> subColumns = new ArrayList<>();

        // 添加主键字段
        Map<String, Object> idColumn = new HashMap<>();
        idColumn.put("columnName", "id");
        idColumn.put("javaField", "id");
        idColumn.put("javaType", "Long");
        idColumn.put("columnComment", "主键ID");
        idColumn.put("isPk", "1");
        idColumn.put("isRequired", "1");
        idColumn.put("isInsert", "1");
        idColumn.put("isEdit", "0");
        idColumn.put("isList", "1");
        idColumn.put("htmlType", "input");
        subColumns.add(idColumn);

        // 添加外键字段
        Map<String, Object> fkColumn = new HashMap<>();
        fkColumn.put("columnName", subTableFkName);
        fkColumn.put("javaField", toCamelCase(subTableFkName));
        fkColumn.put("javaType", "Long");
        fkColumn.put("columnComment", "关联主表ID");
        fkColumn.put("isPk", "0");
        fkColumn.put("isRequired", "1");
        fkColumn.put("isInsert", "1");
        fkColumn.put("isEdit", "1");
        fkColumn.put("isList", "0");
        fkColumn.put("htmlType", "input");
        subColumns.add(fkColumn);

        // 添加示例字段
        Map<String, Object> nameColumn = new HashMap<>();
        nameColumn.put("columnName", "name");
        nameColumn.put("javaField", "name");
        nameColumn.put("javaType", "String");
        nameColumn.put("columnComment", "名称");
        nameColumn.put("isPk", "0");
        nameColumn.put("isRequired", "1");
        nameColumn.put("isInsert", "1");
        nameColumn.put("isEdit", "1");
        nameColumn.put("isList", "1");
        nameColumn.put("htmlType", "input");
        subColumns.add(nameColumn);

        Map<String, Object> remarkColumn = new HashMap<>();
        remarkColumn.put("columnName", "remark");
        remarkColumn.put("javaField", "remark");
        remarkColumn.put("javaType", "String");
        remarkColumn.put("columnComment", "备注");
        remarkColumn.put("isPk", "0");
        remarkColumn.put("isRequired", "0");
        remarkColumn.put("isInsert", "1");
        remarkColumn.put("isEdit", "1");
        remarkColumn.put("isList", "1");
        remarkColumn.put("htmlType", "textarea");
        subColumns.add(remarkColumn);

        subTable.put("columns", subColumns);

        // 设置模板变量
        context.put("subTable", subTable);
        context.put("subTableName", subTableName);
        context.put("subClassName", subClassName);
        context.put("subClassNameLower", subClassNameLower);
        context.put("subTableFkName", subTableFkName);
        context.put("subTableFkJavaField", toCamelCase(subTableFkName));
        context.put("SubTableFkJavaField", StringUtils.capitalize(toCamelCase(subTableFkName)));
    }

    /**
     * 根据字段名查找对应的GenTableColumn对象
     */
    private static GenTableColumn findColumnByName(List<GenTableColumn> columns, String columnName) {
        if (StringUtils.isBlank(columnName) || columns == null) {
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
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return CaseUtils.toCamelCase(str, false, '_');
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
        }

        // Java基础模板
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
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mapperXmlPath = "main/resources/mapper/" + moduleName;
        String vuePath = "src/views/" + moduleName + "/" + businessName;
        String apiPath = "src/api/" + moduleName;
        String typesPath = "src/types/" + moduleName;

        if (template.contains("entity.java.vm")) {
            fileName = String.format("%s/model/entity/%s.java", javaPath, className);
        } else if (template.contains("mapper.java.vm")) {
            fileName = String.format("%s/mapper/%sMapper.java", javaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = String.format("%s/service/%sService.java", javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = String.format("%s/service/impl/%sServiceImpl.java", javaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = String.format("%s/controller/%sController.java", javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = String.format("%s/%sMapper.xml", mapperXmlPath, className);
        } else if (template.contains("list-vo.java.vm")) {
            fileName = String.format("%s/model/vo/%s/%sListVo.java", javaPath, businessName, className);
        } else if (template.contains("vo.java.vm")) {
            fileName = String.format("%s/model/vo/%s/%sVo.java", javaPath, businessName, className);
        } else if (template.contains("add-request.java.vm")) {
            fileName = String.format("%s/model/request/%s/%sAddRequest.java", javaPath, businessName, className);
        } else if (template.contains("update-request.java.vm")) {
            fileName = String.format("%s/model/request/%s/%sUpdateRequest.java", javaPath, businessName, className);
        } else if (template.contains("request.java.vm")) {
            fileName = String.format("%s/model/request/%s/%sQueryRequest.java", javaPath, businessName, className);
        }
        // 前端文件名处理
        else if (template.contains("api.ts.vm")) {
            fileName = String.format("%s/%s.ts", apiPath, businessName);
        } else if (template.contains("types.ts.vm")) {
            fileName = String.format("%s/type%s.ts", typesPath, StringUtils.capitalize(businessName));
        } else if (template.contains("index.vue.vm")) {
            fileName = String.format("%s/index.vue", vuePath);
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
        return String.format("%s.%s", moduleName, businessName);
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
                .filter(StringUtils::isNotBlank)
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
