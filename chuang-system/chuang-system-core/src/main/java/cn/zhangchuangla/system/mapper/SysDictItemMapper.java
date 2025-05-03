package cn.zhangchuangla.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

    /**
     * 获取指定字典下的字典项分页
     *
     * @param page     分页对象
     * @param dictCode 字典编码
     * @param request  查询参数
     * @return 分页数据
     */
    Page<SysDictItem> listSpecifyDictData(Page<SysDictItem> page, @Param("dictCode") String dictCode,
                                          @Param("request") SysDictItemListRequest request);

    /**
     * 分页查询字典数据
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 分页数据
     */
    Page<SysDictItem> listDictData(Page<SysDictItem> page, @Param("request") SysDictItemListRequest request);
}




