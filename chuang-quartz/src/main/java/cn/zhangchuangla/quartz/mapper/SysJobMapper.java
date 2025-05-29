package cn.zhangchuangla.quartz.mapper;

import cn.hutool.db.Page;
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




