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


    /**
     * 查询定时任务组列表
     *
     * @param request 查询参数
     * @return 定时任务组列表
     */
    @Override
    public Page<SysJobGroup> selectJobGroupList(SysJobGroupQueryRequest request) {
        Page<SysJobGroup> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysJobGroupMapper.selectJobGroupList(page, request);
    }


    /**
     * 根据ID获取任务组
     *
     * @param id 任务组ID
     * @return 任务组
     */
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

    /**
     * 新增任务组
     *
     * @param request 新增任务组请求
     * @return 是否添加成功
     */
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

    /**
     * 更新任务组
     *
     * @param request 修改请求
     * @return 修改结果
     */
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

    /**
     * 删除任务组
     *
     * @param ids 任务组ID列表
     * @return 删除结果
     */
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


    /**
     * 查询所有启用的任务组
     */
    @Override
    public List<SysJobGroup> selectEnabledGroups() {
        return sysJobGroupMapper.selectEnabledGroups();
    }

    /**
     * 校验任务组名称是否唯一
     *
     * @param groupName 任务组名称
     * @param id        排除的任务组ID
     * @return true: 存在 false: 不存在
     */
    @Override
    public boolean checkGroupNameExists(String groupName, Long id) {
        return sysJobGroupMapper.checkGroupNameExists(groupName, id) > 0;
    }


    /**
     * 检查任务组编码是否存在
     *
     * @param groupCode 任务组编码
     * @param id        排除的任务组ID
     * @return 是否存在
     */
    @Override
    public boolean checkGroupCodeExists(String groupCode, Long id) {
        return sysJobGroupMapper.checkGroupCodeExists(groupCode, id) > 0;
    }


    /**
     * 获取任务组下的任务数量
     *
     * @param groupId 任务组ID
     * @return 任务数量
     */
    @Override
    public int countJobsByGroupId(Long groupId) {
        return sysJobGroupMapper.countJobsByGroupId(groupId);
    }


    /**
     * 批量修改任务组状态
     *
     * @param ids    任务组ID列表
     * @param status 状态
     * @return 修改结果
     */
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

    /**
     * 导出任务组列表
     *
     * @param request 查询条件
     * @return 任务组列表
     */
    @Override
    public List<SysJobGroup> exportJobGroupList(SysJobGroupQueryRequest request) {
        return sysJobGroupMapper.exportJobGroupList(request);
    }
}
