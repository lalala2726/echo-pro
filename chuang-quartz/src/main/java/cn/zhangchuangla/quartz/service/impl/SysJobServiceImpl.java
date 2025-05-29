package cn.zhangchuangla.quartz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.Page;
import cn.zhangchuangla.quartz.mapper.SysJobMapper;
import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobListQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.service.SysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob>
        implements SysJobService {

    private final SysJobMapper sysJobMapper;


    @Override
    public List<SysJob> listJobs(SysJobListQueryRequest request) {
        Page page = new Page(request.getPageNum(), request.getPageSize());
        return sysJobMapper.listJobs(page, request);
    }

    @Override
    public SysJob getJobById(Long id) {
        return getById(id);
    }

    @Override
    public boolean addJob(SysJobAddRequest request) {
        SysJob sysJob = new SysJob();
        BeanUtil.copyProperties(request, sysJob);
        return save(sysJob);
    }

    @Override
    public boolean updateJob(SysJobUpdateRequest request) {
        SysJob sysJob = new SysJob();
        BeanUtil.copyProperties(request, sysJob);
        return updateById(sysJob);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJob(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    public boolean runJob(Long id) {
        return false;
    }
}




