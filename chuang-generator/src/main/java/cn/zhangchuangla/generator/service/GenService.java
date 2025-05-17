package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.model.TableInfo;

import java.io.IOException;

/**
 * 代码生成服务接口
 *
 * @author Chuang
 */
public interface GenService {

    /**
     * 生成代码
     *
     * @param config 生成配置
     * @return 生成结果
     * @throws IOException IO异常
     */
    boolean generate(GenConfig config) throws IOException;

    /**
     * 获取表信息
     *
     * @param tableName 表名
     * @return 表信息
     */
    TableInfo getTableInfo(String tableName);
}