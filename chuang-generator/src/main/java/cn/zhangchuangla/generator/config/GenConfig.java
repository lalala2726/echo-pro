package cn.zhangchuangla.generator.config;

import lombok.Data;

/**
 * 代码生成配置类
 *
 * @author Chuang
 * <p>
 * Created on 2023-05-18 00:58:05
 */
@Data
public class GenConfig {

    /**
     * 作者
     */
    private String author;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 表前缀
     */
    private String tablePrefix;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 实体类名称(首字母大写)
     */
    private String className;

    /**
     * 实体类名称(首字母小写)
     */
    private String classNameLower;

    /**
     * 生成代码的路径
     */
    private String outputDir;

    /**
     * 是否覆盖已有文件
     */
    private Boolean overwrite = false;
}