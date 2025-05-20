package cn.zhangchuangla.generator.config;

import lombok.Data;

/**
 * 基本配置信息
 *
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:54
 */
@Data
public class GenConfig {

    /**
     * 作者
     */
    private String author = "Chuang";

    /**
     * 包名
     */
    private String packageName = "cn.zhangchuangla.system";

    /**
     * 生成路径
     */
    private String genPath = "/";
}
