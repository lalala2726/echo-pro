package cn.zhangchuangla.quartz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobListQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysJobMapper extends BaseMapper<SysJob> {

    List<SysJob> listJobs(Page page, @Param("request") SysJobListQueryRequest request);
}




