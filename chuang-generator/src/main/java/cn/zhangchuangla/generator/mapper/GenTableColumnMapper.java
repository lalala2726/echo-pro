package cn.zhangchuangla.generator.mapper;

import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
public interface GenTableColumnMapper extends BaseMapper<GenTableColumn> {

    /**
     * 根据表名查询表字段配置
     *
     * @param tableName 表名
     * @return 表字段配置列表
     */
    List<GenTableColumn> selectDbTableColumnsByTableName(@Param("tableName") String tableName);
}




