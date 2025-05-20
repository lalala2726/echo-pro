package cn.zhangchuangla.generator.service.impl;

import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.generator.config.GenConfig;
import cn.zhangchuangla.generator.mapper.GenTableMapper;
import cn.zhangchuangla.generator.model.entity.DatabaseTable;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.DatabaseTableQueryRequest;
import cn.zhangchuangla.generator.model.request.GenConfigUpdateRequest;
import cn.zhangchuangla.generator.model.request.GenTableQueryRequest;
import cn.zhangchuangla.generator.service.GenTableService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
@Service
@RequiredArgsConstructor
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable>
        implements GenTableService {

    private final GenTableMapper genTableMapper;
    private final RedisCache redisCache;


    /**
     * 获取低代码表列表
     *
     * @param request 查询参数
     * @return 列表
     */
    @Override
    public Page<GenTable> listGenTable(GenTableQueryRequest request) {
        Page<GenTable> page = new Page<>(request.getPageNum(), request.getPageSize());
        return genTableMapper.listGenTable(page, request);
    }

    /**
     * 查询当前数据库表信息
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<DatabaseTable> listDatabaseTables(DatabaseTableQueryRequest request) {
        Page<DatabaseTable> page = new Page<>(request.getPageNum(), request.getPageSize());
        return genTableMapper.listDatabaseTables(page, request);
    }

    /**
     * 将当前数据库中的表导入到低代码表中
     *
     * @param tableNames 表名称集合
     * @return 操作结果
     */
    @Override
    public boolean importTable(List<String> tableNames) {
        // 校验表是否存在
        for (String tableName : tableNames) {
            // 查询当前表名是否已存在于低代码表中
            List<GenTable> existingTables = list();
            boolean exists = existingTables.stream()
                    .anyMatch(genTable -> genTable.getTableName().equalsIgnoreCase(tableName));
            if (exists) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "表 " + tableName + " 已存在，无法重复导入");
            }
        }

        return false;
    }

    /**
     * 获取低代码表的配置信息，优先从 Redis 缓存中获取，若不存在则创建一个默认配置并缓存。
     *
     * @return 返回当前的低代码表配置信息，始终不为 null。
     */
    @Override
    public GenConfig getConfigInfo() {
        String cacheKey = RedisConstants.Generator.CONFIG_INFO;
        GenConfig genConfigCache = redisCache.getCacheObject(cacheKey);
        if (genConfigCache == null) {
            GenConfig genConfig = new GenConfig();
            redisCache.setCacheObject(cacheKey, genConfig);
            return genConfig;
        }
        return genConfigCache;
    }

    /**
     * 修改配置信息
     *
     * @param request 新配置信息
     * @return 操作结果
     */
    @Override
    public boolean updateConfigInfo(GenConfigUpdateRequest request) {
        GenConfig genConfig = new GenConfig();
        BeanUtils.copyProperties(request, genConfig);
        redisCache.setCacheObject(RedisConstants.Generator.CONFIG_INFO, genConfig);
        return true;
    }
}




