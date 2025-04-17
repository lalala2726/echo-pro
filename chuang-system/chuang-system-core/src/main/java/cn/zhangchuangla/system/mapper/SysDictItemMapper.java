package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author zhangchuang
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
    Page<SysDictItem> listSpecifyDictData(Page<SysDictItem> page, String dictCode, SysDictItemListRequest request);

    /**
     * 分页查询字典数据
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 分页数据
     */
    Page<SysDictItem> listDictData(Page<SysDictItem> page, SysDictItemListRequest request);
}




