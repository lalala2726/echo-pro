package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 字典类型 Mapper 接口
 *
 * @author Chuang
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 分页查询字典类型列表
     *
     * @param page    分页对象
     * @param request 查询条件
     * @return 字典类型分页列表
     */
    Page<SysDictType> listDictType(Page<SysDictType> page, @Param("request") SysDictTypeQueryRequest request);
} 