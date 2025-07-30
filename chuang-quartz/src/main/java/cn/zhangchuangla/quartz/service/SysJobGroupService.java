package cn.zhangchuangla.quartz.service;

import cn.zhangchuangla.quartz.entity.SysJobGroup;
import cn.zhangchuangla.quartz.model.request.SysJobGroupAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobGroupQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobGroupUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobGroupVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 定时任务组服务接口
 *
 * @author Chuang
 */
public interface SysJobGroupService extends IService<SysJobGroup> {

    /**
     * 分页查询任务组列表
     *
     * @param request 查询条件
     * @return 任务组分页列表
     */
    Page<SysJobGroup> selectJobGroupList(SysJobGroupQueryRequest request);

    /**
     * 根据ID查询任务组详情
     *
     * @param id 任务组ID
     * @return 任务组详情
     */
    SysJobGroupVo selectJobGroupById(Long id);

    /**
     * 新增任务组
     *
     * @param request 新增请求
     * @return 是否成功
     */
    boolean addJobGroup(SysJobGroupAddRequest request);

    /**
     * 修改任务组
     *
     * @param request 修改请求
     * @return 是否成功
     */
    boolean updateJobGroup(SysJobGroupUpdateRequest request);

    /**
     * 删除任务组
     *
     * @param ids 任务组ID列表
     * @return 是否成功
     */
    boolean deleteJobGroups(List<Long> ids);

    /**
     * 查询所有启用的任务组
     *
     * @return 任务组列表
     */
    List<SysJobGroup> selectEnabledGroups();

    /**
     * 检查任务组名称是否存在
     *
     * @param groupName 任务组名称
     * @param id        排除的任务组ID
     * @return 是否存在
     */
    boolean checkGroupNameExists(String groupName, Long id);

    /**
     * 检查任务组编码是否存在
     *
     * @param groupCode 任务组编码
     * @param id        排除的任务组ID
     * @return 是否存在
     */
    boolean checkGroupCodeExists(String groupCode, Long id);

    /**
     * 检查任务组下是否有任务
     *
     * @param groupId 任务组ID
     * @return 任务数量
     */
    int countJobsByGroupId(Long groupId);

    /**
     * 批量更新任务组状态
     *
     * @param ids    任务组ID列表
     * @param status 状态
     * @return 是否成功
     */
    boolean updateJobGroupStatus(List<Long> ids, Integer status);
}
