package cn.zhangchuangla.generator.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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
        velocityContext.put("functionName", StrUtil.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StrUtil.lowerFirst(genTable.getClassName()));
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

        return velocityContext;
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList() {
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/entity.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/java/vo.java.vm");
        templates.add("vm/java/list-vo.java.vm");
        templates.add("vm/java/request.java.vm");
        templates.add("vm/java/add-request.java.vm");
        templates.add("vm/java/update-request.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
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

        String javaPath = PROJECT_PATH + "/" + StrUtil.replace(packageName, ".", "/");
        String mapperXmlPath = "main/resources/mapper/" + moduleName;

        if (template.contains("entity.java.vm")) {
            fileName = StrUtil.format("{}/model/entity/{}.java", javaPath, className);
        } else if (template.contains("mapper.java.vm")) {
            fileName = StrUtil.format("{}/mapper/{}Mapper.java", javaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StrUtil.format("{}/service/{}Service.java", javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StrUtil.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = StrUtil.format("{}/controller/{}Controller.java", javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StrUtil.format("{}/{}Mapper.xml", mapperXmlPath, className);
        } else if (template.contains("list-vo.java.vm")) {
            fileName = StrUtil.format("{}/model/vo/{}/{}ListVo.java", javaPath, businessName, className);
        } else if (template.contains("vo.java.vm")) {
            fileName = StrUtil.format("{}/model/vo/{}/{}Vo.java", javaPath, businessName, className);
        } else if (template.contains("add-request.java.vm")) {
            fileName = StrUtil.format("{}/model/request/{}/{}AddRequest.java", javaPath, businessName, className);
        } else if (template.contains("update-request.java.vm")) {
            fileName = StrUtil.format("{}/model/request/{}/{}UpdateRequest.java", javaPath, businessName, className);
        } else if (template.contains("request.java.vm")) {
            fileName = StrUtil.format("{}/model/request/{}/{}QueryRequest.java", javaPath, businessName, className);
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
            if ("Date".equals(column.getJavaType())) {
                importList.add("java.util.Date");
            } else if ("BigDecimal".equals(column.getJavaType())) {
                importList.add("java.math.BigDecimal");
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
            p.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.name());
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
