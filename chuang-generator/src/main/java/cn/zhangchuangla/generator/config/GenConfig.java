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
//todo 后续将这个到缓存中，后续前端可以修改基本基本的配置
public class GenConfig {

    /**
     * 作者
     */
    private String author = "Chuang";

    /**
     * 包名
     */
    private String packageName = "cn.zhangchuangla.system";
}
