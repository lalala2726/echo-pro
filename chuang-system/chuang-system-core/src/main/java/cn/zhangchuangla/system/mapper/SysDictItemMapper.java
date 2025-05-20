package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

    /**
     * 查询字典项列表
     *
     * @param sysDictItemPage 分页对象
     * @param request         查询参数
     * @return 字典项列表
     */
    Page<SysDictItem> listDictItem(Page<SysDictItem> sysDictItemPage, @Param("dictType") String dictType,
                                   @Param("request") SysDictItemQueryRequest request);
}




