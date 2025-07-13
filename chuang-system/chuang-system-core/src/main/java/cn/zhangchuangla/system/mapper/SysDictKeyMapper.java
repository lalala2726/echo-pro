package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictKey;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysDictKeyMapper extends BaseMapper<SysDictKey> {

    /**
     * 查询字典类型列表
     *
     * @param sysDictKeyPage 分页对象
     * @param request        查询参数
     * @return 字典类型列表
     */
    Page<SysDictKey> listDictKey(Page<SysDictKey> sysDictKeyPage, @Param("request") SysDictKeyQueryRequest request);
}




