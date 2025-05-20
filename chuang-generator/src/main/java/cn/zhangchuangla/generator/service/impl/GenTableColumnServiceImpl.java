package cn.zhangchuangla.generator.service.impl;

import cn.zhangchuangla.generator.mapper.GenTableColumnMapper;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.service.GenTableColumnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
@Service
@RequiredArgsConstructor
public class GenTableColumnServiceImpl extends ServiceImpl<GenTableColumnMapper, GenTableColumn>
        implements GenTableColumnService {

    private final GenTableColumnMapper genTableColumnMapper;

    /**
     * 根据表名查询表字段配置
     *
     * @param tableName 表名
     * @return 表字段配置列表
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableName(String tableName) {
        return genTableColumnMapper.selectDbTableColumnsByTableName(tableName);
    }
}




