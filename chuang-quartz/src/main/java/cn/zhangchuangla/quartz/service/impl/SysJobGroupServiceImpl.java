package cn.zhangchuangla.quartz.service.impl;

import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.quartz.entity.SysJobGroup;
import cn.zhangchuangla.quartz.mapper.SysJobGroupMapper;
import cn.zhangchuangla.quartz.model.request.SysJobGroupAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobGroupQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobGroupUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobGroupVo;
import cn.zhangchuangla.quartz.service.SysJobGroupService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务组服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobGroupServiceImpl extends ServiceImpl<SysJobGroupMapper, SysJobGroup> implements SysJobGroupService {

    private final SysJobGroupMapper sysJobGroupMapper;

    @Override
    public Page<SysJobGroup> selectJobGroupList(SysJobGroupQueryRequest request) {
        Page<SysJobGroup> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysJobGroupMapper.selectJobGroupList(page, request);
    }

    @Override
    public SysJobGroupVo selectJobGroupById(Long id) {
        SysJobGroup jobGroup = getById(id);
        if (jobGroup == null) {
            return null;
        }

        SysJobGroupVo vo = new SysJobGroupVo();
        BeanUtils.copyProperties(jobGroup, vo);

        // 设置状态描述
        vo.setStatusDesc(jobGroup.getStatus() == 0 ? "正常" : "停用");

        // 查询任务数量
        int jobCount = sysJobGroupMapper.countJobsByGroupId(id);
        vo.setJobCount(jobCount);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addJobGroup(SysJobGroupAddRequest request) {
        // 验证任务组名称是否存在
        if (checkGroupNameExists(request.getGroupName(), null)) {
            throw new ServiceException("任务组名称已存在");
        }

        // 验证任务组编码是否存在
        if (checkGroupCodeExists(request.getGroupCode(), null)) {
            throw new ServiceException("任务组编码已存在");
        }

        SysJobGroup jobGroup = new SysJobGroup();
        BeanUtils.copyProperties(request, jobGroup);

        // 设置默认值
        if (jobGroup.getStatus() == null) {
            jobGroup.setStatus(0);
        }
        if (jobGroup.getSort() == null) {
            jobGroup.setSort(0);
        }

        return save(jobGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJobGroup(SysJobGroupUpdateRequest request) {
        // 检查任务组是否存在
        SysJobGroup existingGroup = getById(request.getId());
        if (existingGroup == null) {
            throw new ServiceException("任务组不存在");
        }

        // 验证任务组名称是否存在
        if (checkGroupNameExists(request.getGroupName(), request.getId())) {
            throw new ServiceException("任务组名称已存在");
        }

        // 验证任务组编码是否存在
        if (checkGroupCodeExists(request.getGroupCode(), request.getId())) {
            throw new ServiceException("任务组编码已存在");
        }

        SysJobGroup jobGroup = new SysJobGroup();
        BeanUtils.copyProperties(request, jobGroup);

        return updateById(jobGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobGroups(List<Long> ids) {
        for (Long id : ids) {
            // 检查任务组下是否有任务
            int jobCount = countJobsByGroupId(id);
            if (jobCount > 0) {
                SysJobGroup jobGroup = getById(id);
                String groupName = jobGroup != null ? jobGroup.getGroupName() : "未知";
                throw new ServiceException("任务组【" + groupName + "】下存在任务，无法删除");
            }
        }

        return removeByIds(ids);
    }

    @Override
    public List<SysJobGroup> selectEnabledGroups() {
        return sysJobGroupMapper.selectEnabledGroups();
    }

    @Override
    public boolean checkGroupNameExists(String groupName, Long id) {
        return sysJobGroupMapper.checkGroupNameExists(groupName, id) > 0;
    }

    @Override
    public boolean checkGroupCodeExists(String groupCode, Long id) {
        return sysJobGroupMapper.checkGroupCodeExists(groupCode, id) > 0;
    }

    @Override
    public int countJobsByGroupId(Long groupId) {
        return sysJobGroupMapper.countJobsByGroupId(groupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJobGroupStatus(List<Long> ids, Integer status) {
        for (Long id : ids) {
            SysJobGroup jobGroup = new SysJobGroup();
            jobGroup.setId(id);
            jobGroup.setStatus(status);
            updateById(jobGroup);
        }
        return true;
    }
}
