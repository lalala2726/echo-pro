package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.request.dict.SysDictListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysDictMapper extends BaseMapper<SysDict> {

    /**
     * 分页查询字典
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 分页数据
     */
    Page<SysDict> listDict(Page<SysDict> page, @Param("request") SysDictListRequest request);
}




