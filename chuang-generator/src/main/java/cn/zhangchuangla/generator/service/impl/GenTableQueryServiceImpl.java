package cn.zhangchuangla.generator.service.impl;

import cn.zhangchuangla.generator.mapper.GenTableMapper;
import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.service.GenTableColumnService;
import cn.zhangchuangla.generator.service.GenTableQueryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 代码生成表查询服务实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class GenTableQueryServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements GenTableQueryService {

  private final GenTableColumnService genTableColumnService;

  @Override
  @Transactional(readOnly = true)
  public GenTable selectGenTableById(Long id) {
    GenTable genTable = this.getById(id); // this.getById 是 ServiceImpl (indirectly from BaseMapper) 的方法
    if (genTable != null) {
      List<GenTableColumn> columns = genTableColumnService.selectGenTableColumnListByTableId(id);
      genTable.setColumns(columns);
    }
    return genTable;
  }

  @Override
  @Transactional(readOnly = true)
  public GenTable selectGenTableByName(String tableName) {
    GenTable genTable = this.getOne(new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, tableName));
    if (genTable != null) {
      List<GenTableColumn> columns = genTableColumnService.selectGenTableColumnListByTableId(genTable.getTableId());
      genTable.setColumns(columns);
    }
    return genTable;
  }
}