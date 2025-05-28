package cn.zhangchuangla.quartz.service.impl;

import cn.zhangchuangla.quartz.mapper.SysJobMapper;
import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobListQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.service.SysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chuang
 */
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob>
        implements SysJobService {

    @Override
    public List<SysJob> listJobs(SysJobListQueryRequest request) {
        return List.of();
    }

    @Override
    public SysJob getJobById(Long id) {
        return null;
    }

    @Override
    public boolean addJob(SysJobAddRequest request) {
        return false;
    }

    @Override
    public boolean updateJob(SysJobUpdateRequest request) {
        return false;
    }

    @Override
    public boolean deleteJob(List<Long> ids) {
        return false;
    }

    @Override
    public boolean runJob(Long id) {
        return false;
    }
}




