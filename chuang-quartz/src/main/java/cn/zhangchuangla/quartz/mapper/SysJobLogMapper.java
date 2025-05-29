package cn.zhangchuangla.quartz.mapper;

import cn.zhangchuangla.quartz.model.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobLogListQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    List<SysJobLog> listJobLogs(Page<SysJobLog> page,@Param("request") SysJobLogListQueryRequest request);

    void cleanJobLog();

}




