package cn.zhangchuangla.quartz.service;

import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobListQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysJobService extends IService<SysJob> {

    List<SysJob> listJobs(SysJobListQueryRequest request);

    SysJob getJobById(Long id);

    boolean addJob(SysJobAddRequest request);

    boolean updateJob(SysJobUpdateRequest request);

    boolean deleteJob(List<Long> ids);

    boolean runJob(Long id);
}
