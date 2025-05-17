package cn.zhangchuangla.generator.service;

import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 代码生成业务表字段 服务层
 *
 * @author Chuang
 */
public interface GenTableColumnService extends IService<GenTableColumn> {

    /**
     * 根据表ID查询列信息
     *
     * @param tableId 表ID
     * @return 列信息列表
     */
    List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);

    /**
     * 根据表ID删除列信息
     *
     * @param tableId 表ID
     * @return 结果
     */
    int deleteGenTableColumnByTableId(Long tableId);

    /**
     * 根据多个表ID删除列信息
     *
     * @param tableIds 表ID数组
     * @return 结果
     */
    int deleteGenTableColumnByTableIds(Long[] tableIds);
}