package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictValue;
import cn.zhangchuangla.system.model.request.dict.SysDictValueQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysDictValueMapper extends BaseMapper<SysDictValue> {

    /**
     * 查询字典项列表
     *
     * @param sysDictValuePage 分页对象
     * @param request          查询参数
     * @return 字典项列表
     */
    Page<SysDictValue> listDictValue(Page<SysDictValue> sysDictValuePage, @Param("dictKey") String dictKey,
                                     @Param("request") SysDictValueQueryRequest request);
}




