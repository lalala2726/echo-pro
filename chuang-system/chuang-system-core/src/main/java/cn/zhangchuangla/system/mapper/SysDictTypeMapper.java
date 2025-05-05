package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 查询字典类型列表
     *
     * @param sysDictTypePage 分页对象
     * @param request         查询参数
     * @return 字典类型列表
     */
    Page<SysDictType> listDictType(Page<SysDictType> sysDictTypePage, @Param("request") SysDictTypeListRequest request);
}




