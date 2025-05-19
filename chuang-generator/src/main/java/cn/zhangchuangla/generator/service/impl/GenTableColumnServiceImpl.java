package cn.zhangchuangla.generator.service.impl;

import cn.zhangchuangla.generator.mapper.GenTableColumnMapper;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.service.GenTableColumnService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 代码生成业务表字段 服务层实现
 *
 * @author Chuang
 */
@Service
public class GenTableColumnServiceImpl extends ServiceImpl<GenTableColumnMapper, GenTableColumn>
        implements GenTableColumnService {

    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId) {
        return list(new LambdaQueryWrapper<GenTableColumn>()
                .eq(GenTableColumn::getTableId, tableId)
                .orderByAsc(GenTableColumn::getSort));
    }

    @Override
    public int deleteGenTableColumnByTableId(Long tableId) {
        return baseMapper.delete(new LambdaQueryWrapper<GenTableColumn>().eq(GenTableColumn::getTableId, tableId));
    }

    @Override
    public int deleteGenTableColumnByTableIds(Long[] tableIds) {
        return baseMapper
                .delete(new LambdaQueryWrapper<GenTableColumn>().in(GenTableColumn::getTableId, Arrays.asList(tableIds)));
    }
}