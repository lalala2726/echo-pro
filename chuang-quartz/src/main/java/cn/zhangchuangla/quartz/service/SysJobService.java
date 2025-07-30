package cn.zhangchuangla.quartz.service;

import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobBatchRequest;
import cn.zhangchuangla.quartz.model.request.SysJobQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 定时任务服务接口
 *
 * @author Chuang
 */
public interface SysJobService extends IService<SysJob> {

    /**
     * 分页查询定时任务列表
     *
     * @param request 查询条件
     * @return 定时任务分页列表
     */
    Page<SysJob> selectJobList(SysJobQueryRequest request);

    /**
     * 根据任务ID查询任务详情
     *
     * @param jobId 任务ID
     * @return 任务详情
     */
    SysJobVo selectJobById(Long jobId);

    /**
     * 新增定时任务
     *
     * @param request 任务信息
     * @return 操作结果
     */
    boolean addJob(SysJobAddRequest request);

    /**
     * 修改定时任务
     *
     * @param request 任务信息
     * @return 操作结果
     */
    boolean updateJob(SysJobUpdateRequest request);

    /**
     * 删除定时任务
     *
     * @param jobIds 任务ID列表
     * @return 操作结果
     */
    boolean deleteJobs(List<Long> jobIds);

    /**
     * 启动任务
     *
     * @param jobId 任务ID
     * @return 操作结果
     */
    boolean startJob(Long jobId);

    /**
     * 暂停任务
     *
     * @param jobId 任务ID
     * @return 操作结果
     */
    boolean pauseJob(Long jobId);

    /**
     * 恢复任务
     *
     * @param jobId 任务ID
     * @return 操作结果
     */
    boolean resumeJob(Long jobId);

    /**
     * 立即执行任务
     *
     * @param jobId 任务ID
     * @return 操作结果
     */
    boolean runJob(Long jobId);

    /**
     * 批量操作任务
     *
     * @param request 批量操作请求
     * @return 操作结果
     */
    boolean batchOperateJobs(SysJobBatchRequest request);

    /**
     * 检查任务名称是否存在
     *
     * @param jobName 任务名称
     * @param jobId   排除的任务ID
     * @return 是否存在
     */
    boolean checkJobNameExists(String jobName, Long jobId);

    /**
     * 获取所有启用的任务
     *
     * @return 任务列表
     */
    List<SysJob> selectEnabledJobs();

    /**
     * 根据任务组查询任务列表
     *
     * @param jobGroup 任务组名
     * @return 任务列表
     */
    List<SysJob> selectJobsByGroup(String jobGroup);

    /**
     * 检查任务依赖关系
     *
     * @param jobId 任务ID
     * @return 依赖的任务列表
     */
    List<SysJob> checkJobDependencies(Long jobId);

    /**
     * 初始化定时任务
     */
    void initJobs();

    /**
     * 刷新任务状态
     *
     * @param jobId 任务ID
     */
    void refreshJobStatus(Long jobId);
}
