package cn.zhangchuangla.quartz.service;

import cn.zhangchuangla.quartz.model.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobLogListQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysJobLogService extends IService<SysJobLog> {

    List<SysJobLog> listJobLogs(SysJobLogListQueryRequest request);

    SysJobLog getJobLogById(Long id);

    boolean deleteJobLog(List<Long> ids);

    void cleanJobLog();
}
