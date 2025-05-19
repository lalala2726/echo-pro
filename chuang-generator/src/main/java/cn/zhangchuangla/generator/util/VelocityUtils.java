package cn.zhangchuangla.generator.util;

import cn.zhangchuangla.generator.model.TableInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity工具类
 *
 * @author Chuang
 */
public class VelocityUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = System.getProperty("user.dir");

    /**
     * 模板路径
     */
    private static final String TEMPLATE_PATH = "template";

    /**
     * 默认上级模板挂载点
     */
    private static final String PARENT_PATH = "main/resources/";

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
            // 初始化Velocity引擎
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前日期
     */
    public static String getDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 渲染模板
     *
     * @param templatePath 模板路径
     * @param context      模板上下文
     * @return 渲染后的内容
     */
    public static String render(String templatePath, Map<String, Object> context) {
        StringWriter sw = new StringWriter();
        Template tpl = Velocity.getTemplate(templatePath, StandardCharsets.UTF_8.name());
        tpl.merge(new VelocityContext(context), sw);
        return sw.toString();
    }

    /**
     * 生成代码文件
     *
     * @param templatePath 模板路径
     * @param context      模板上下文
     * @param outputFile   输出文件
     */
    public static void generate(String templatePath, Map<String, Object> context, String outputFile)
            throws IOException {
        String content = render(templatePath, context);
        File file = new File(outputFile);
        FileUtils.forceMkdirParent(file);
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
    }

    /**
     * 新增辅助方法：获取模板路径
     */
    public static String getTemplatePath(TableInfo tableInfo, String templateFileName) {
        String baseTemplatePath = TEMPLATE_PATH + "/";
        if (tableInfo.getTableType() != null) {
            String specificPath = "";
            switch (tableInfo.getTableType()) {
                case MASTER_CHILD:
                    specificPath = "master_child/";
                    break;
                case TREE:
                    specificPath = "tree/";
                    break;
                case SINGLE:
                default:
                    // 单表或默认情况，路径为空字符串，直接使用根template目录下的模板
                    break;
            }
            // 检查特定类型的模板是否存在
            String fullSpecificPath = baseTemplatePath + specificPath + templateFileName;
            if (Velocity.resourceExists(fullSpecificPath)) {
                return fullSpecificPath;
            }
        }
        // 如果特定模板不存在或表类型为空，则使用通用模板
        return baseTemplatePath + templateFileName;
    }

    /**
     * 生成实体类代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateEntity(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String packagePath = getPackagePath(tableInfo.getPackageName());
        String entityPath = outputDir + "/" + packagePath + "/" + tableInfo.getModuleName() + "/model/entity/"
                + tableInfo.getClassName() + ".java";
        generate(getTemplatePath(tableInfo, "entity.java.vm"), context, entityPath);
    }

    /**
     * 生成Mapper接口代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateMapper(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String packagePath = getPackagePath(tableInfo.getPackageName());
        String mapperPath = outputDir + "/" + packagePath + "/" + tableInfo.getModuleName() + "/mapper/"
                + tableInfo.getClassName() + "Mapper.java";
        generate(getTemplatePath(tableInfo, "mapper.java.vm"), context, mapperPath);
    }

    /**
     * 生成Mapper XML代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateMapperXml(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String xmlPath = outputDir + "/resources/mapper/" + tableInfo.getModuleName() + "/" + tableInfo.getClassName()
                + "Mapper.xml";
        generate(getTemplatePath(tableInfo, "mapper.xml.vm"), context, xmlPath);
    }

    /**
     * 生成Service接口代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateService(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String packagePath = getPackagePath(tableInfo.getPackageName());
        String servicePath = outputDir + "/" + packagePath + "/" + tableInfo.getModuleName() + "/service/"
                + tableInfo.getClassName() + "Service.java";
        generate(getTemplatePath(tableInfo, "service.java.vm"), context, servicePath);
    }

    /**
     * 生成Service实现类代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateServiceImpl(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String packagePath = getPackagePath(tableInfo.getPackageName());
        String implPath = outputDir + "/" + packagePath + "/" + tableInfo.getModuleName() + "/service/impl/"
                + tableInfo.getClassName() + "ServiceImpl.java";
        generate(getTemplatePath(tableInfo, "serviceImpl.java.vm"), context, implPath);
    }

    /**
     * 生成Controller代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateController(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String packagePath = getPackagePath(tableInfo.getPackageName());
        String controllerPath = outputDir + "/" + packagePath + "/" + tableInfo.getModuleName() + "/controller/"
                + tableInfo.getClassName() + "Controller.java";
        generate(getTemplatePath(tableInfo, "controller.java.vm"), context, controllerPath);
    }

    /**
     * 生成请求类代码
     *
     * @param tableInfo 表信息
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public static void generateRequestClasses(TableInfo tableInfo, String outputDir) throws IOException {
        Map<String, Object> context = GenUtils.getTemplateVariables(tableInfo);
        String packagePath = getPackagePath(tableInfo.getPackageName());
        String requestBasePath = outputDir + "/" + packagePath + "/" + tableInfo.getModuleName() + "/model/request/"
                + tableInfo.getClassNameLower() + "/";

        // 生成添加请求类
        String addRequestPath = requestBasePath + tableInfo.getClassName() + "AddRequest.java";
        generate(getTemplatePath(tableInfo, "request/add.java.vm"), context, addRequestPath);

        // 生成更新请求类
        String updateRequestPath = requestBasePath + tableInfo.getClassName() + "UpdateRequest.java";
        generate(getTemplatePath(tableInfo, "request/update.java.vm"), context, updateRequestPath);

        // 生成列表查询请求类
        String listRequestPath = requestBasePath + tableInfo.getClassName() + "ListRequest.java";
        generate(getTemplatePath(tableInfo, "request/list.java.vm"), context, listRequestPath);
    }

    /**
     * 获取包路径
     *
     * @param packageName 包名
     * @return 包路径
     */
    public static String getPackagePath(String packageName) {
        return "src/main/java/" + StringUtils.replace(packageName, ".", "/");
    }
}