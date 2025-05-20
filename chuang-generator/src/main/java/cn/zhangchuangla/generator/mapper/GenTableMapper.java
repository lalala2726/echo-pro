package cn.zhangchuangla.generator.mapper;

import cn.zhangchuangla.generator.model.entity.GenTable;
import cn.zhangchuangla.generator.model.request.GenTableListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
public interface GenTableMapper extends BaseMapper<GenTable> {

    Page<GenTable> listGenTable(Page<GenTable> page, @Param("request") GenTableListRequest request);
}




