package cn.zhangchuangla.generator.mapper;

import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import cn.zhangchuangla.generator.model.vo.DbTableVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成业务表 Mapper接口
 *
 * @author Chuang
 */
public interface GenTableMapper extends BaseMapper<GenTable> {

    /**
     * 查询数据库表列表
     *
     * @param tableName 表名称 (可包含模糊匹配字符)
     * @return 数据库表集合
     */
    List<DbTableVO> selectDbTableList(@Param("tableName") String tableName);

    /**
     * 根据表名称查询数据库表信息
     *
     * @param tableName 表名称
     * @return 数据库表信息
     */
    DbTableVO selectDbTableByName(@Param("tableName") String tableName);

    /**
     * 根据表名称查询数据库表列信息
     *
     * @param tableName 表名称
     * @return 表列信息集合
     */
    List<GenTableColumn> selectDbTableColumnsByName(@Param("tableName") String tableName);

    /**
     * 查询数据库表列表（排除已导入的表）
     *
     * @param tableName 表名称
     * @return 数据库表集合
     */
    List<DbTableVO> selectDbTableListExcludeGenTable(@Param("tableName") String tableName);

    /**
     * 执行DDL语句
     *
     * @param sql DDL SQL语句
     */
    void executeDDL(@Param("sql") String sql);
}
