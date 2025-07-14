package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictData;
import cn.zhangchuangla.system.model.request.dict.SysDictDataQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 字典数据 Mapper 接口
 *
 * @author Chuang
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 分页查询字典数据列表
     *
     * @param page     分页对象
     * @param dictType 字典类型
     * @param request  查询条件
     * @return 字典数据分页列表
     */
    Page<SysDictData> listDictData(Page<SysDictData> page, @Param("dictType") String dictType, @Param("request") SysDictDataQueryRequest request);
} 