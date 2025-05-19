package cn.zhangchuangla.generator.util;

import cn.zhangchuangla.generator.model.TableField;
import cn.zhangchuangla.generator.model.TableInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成工具类
 *
 * @author Chuang
 */
public class GenUtils {

    /**
     * Entity后缀
     */
    private static final String ENTITY_SUFFIX = "";

    /**
     * Mapper后缀
     */
    private static final String MAPPER_SUFFIX = "Mapper";

    /**
     * Service后缀
     */
    private static final String SERVICE_SUFFIX = "Service";

    /**
     * ServiceImpl后缀
     */
    private static final String SERVICE_IMPL_SUFFIX = "ServiceImpl";

    /**
     * Controller后缀
     */
    private static final String CONTROLLER_SUFFIX = "Controller";

    /**
     * 数据库字符串类型
     */
    private static final String[] COLUMN_TYPE_STR = {"char", "varchar", "nvarchar", "varchar2", "tinytext", "text",
            "mediumtext", "longtext"};

    /**
     * 数据库时间类型
     */
    private static final String[] COLUMN_TYPE_TIME = {"datetime", "time", "date", "timestamp"};

    /**
     * 数据库数字类型
     */
    private static final String[] COLUMN_TYPE_NUMBER = {"tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bit", "bigint", "float", "double", "decimal"};

    /**
     * 初始化表信息
     */
    public static void initTableInfo(TableInfo tableInfo) {
        // 表名转换成Java类名
        String className = convertToCamelCase(tableInfo.getTableName());
        tableInfo.setClassName(className);
        tableInfo.setClassNameLower(StringUtils.uncapitalize(className));
    }

    /**
     * 初始化列属性字段
     */
    public static void initTableField(TableField field) {
        String dataType = getDbType(field.getColumnType());
        String columnName = field.getColumnName();
        // 设置java字段名
        field.setJavaField(toCamelCase(columnName));
        field.setJavaFieldCapitalize(convertToCamelCase(columnName));

        if (arraysContains(COLUMN_TYPE_STR, dataType)) {
            field.setJavaType(String.class.getSimpleName());
        } else if (arraysContains(COLUMN_TYPE_TIME, dataType)) {
            field.setJavaType("LocalDateTime");
        } else if (arraysContains(COLUMN_TYPE_NUMBER, dataType)) {
            // 如果是浮点型
            String[] str = StringUtils.split(StringUtils.substringBetween(field.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
                field.setJavaType("BigDecimal");
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
                field.setJavaType(Integer.class.getSimpleName());
            }
            // 长整形
            else {
                field.setJavaType(Long.class.getSimpleName());
            }
        }
    }

    /**
     * 获取模板变量信息
     *
     * @return 模板变量信息
     */
    public static Map<String, Object> getTemplateVariables(TableInfo tableInfo) {
        Map<String, Object> context = new HashMap<>();
        context.put("tableName", tableInfo.getTableName());
        context.put("tableComment", tableInfo.getTableComment());
        context.put("primaryKey", tableInfo.getPrimaryKey());
        context.put("className", tableInfo.getClassName());
        context.put("classNameLower", tableInfo.getClassNameLower());
        context.put("packageName", tableInfo.getPackageName());
        context.put("moduleName", tableInfo.getModuleName());
        context.put("author", tableInfo.getAuthor());
        context.put("fields", tableInfo.getFields());
        context.put("datetime", VelocityUtils.getDate());
        context.put("tableType", tableInfo.getTableType() != null ? tableInfo.getTableType().name() : null);

        // 主子表相关字段
        context.put("subTableName", tableInfo.getSubTableName());
        context.put("subClassName", tableInfo.getSubClassName());
        context.put("subClassNameLower", tableInfo.getSubClassNameLower());
        context.put("subTableFkName", tableInfo.getSubTableFkName());
        context.put("subTableFkNameCapitalized", tableInfo.getSubTableFkNameCapitalized());
        context.put("subTableListName", tableInfo.getSubTableListName());

        // 树形表相关字段
        context.put("treeCode", tableInfo.getTreeCode());
        context.put("treeCodeCapitalized", tableInfo.getTreeCodeCapitalized());
        context.put("treeParentCode", tableInfo.getTreeParentCode());
        context.put("treeParentCodeCapitalized", tableInfo.getTreeParentCodeCapitalized());
        context.put("treeName", tableInfo.getTreeName());
        context.put("treeNameCapitalized", tableInfo.getTreeNameCapitalized());

        // 实体类名（首字母大写）
        String className = tableInfo.getClassName();

        // 实体类名（首字母小写）
        String classNameLower = StringUtils.uncapitalize(className);

        // Entity
        context.put("entityName", className + ENTITY_SUFFIX);

        // Mapper
        context.put("mapperName", className + MAPPER_SUFFIX);

        // Service
        context.put("serviceName", className + SERVICE_SUFFIX);

        // ServiceImpl
        context.put("serviceImplName", className + SERVICE_IMPL_SUFFIX);

        // Controller
        context.put("controllerName", className + CONTROLLER_SUFFIX);

        return context;
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (StringUtils.indexOf(columnType, "(") > 0) {
            return StringUtils.substringBefore(columnType, "(");
        } else {
            return columnType;
        }
    }

    /**
     * 判断是否包含
     *
     * @param arr         数组
     * @param targetValue 目标值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 下划线转驼峰命名
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰命名（首字母大写）
     */
    public static String convertToCamelCase(String s) {
        String camelCase = toCamelCase(s);
        return StringUtils.capitalize(camelCase);
    }
}