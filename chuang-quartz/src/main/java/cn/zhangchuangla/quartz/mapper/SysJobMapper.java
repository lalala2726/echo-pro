package cn.zhangchuangla.quartz.mapper;

import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务 Mapper 接口
 *
 * @author Chuang
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

    /**
     * 分页查询定时任务列表
     *
     * @param page    分页对象
     * @param request 查询条件
     * @return 定时任务分页列表
     */
    Page<SysJob> selectJobList(Page<SysJob> page, @Param("request") SysJobQueryRequest request);

    /**
     * 查询所有启用的定时任务
     *
     * @return 定时任务列表
     */
    List<SysJob> selectEnabledJobs();

    /**
     * 根据依赖任务ID查询任务列表
     *
     * @param dependentJobId 依赖任务ID
     * @return 任务列表
     */
    List<SysJob> selectJobsByDependentId(@Param("dependentJobId") Long dependentJobId);

    /**
     * 批量更新任务状态
     *
     * @param jobIds 任务ID列表
     * @param status 状态
     * @return 更新数量
     */
    int batchUpdateStatus(@Param("jobIds") List<Long> jobIds, @Param("status") Integer status);

    /**
     * 检查任务名称是否存在
     *
     * @param jobName 任务名称
     * @param jobId   排除的任务ID
     * @return 数量
     */
    int checkJobNameExists(@Param("jobName") String jobName, @Param("jobId") Long jobId);

    /**
     * 获取任务的依赖关系
     *
     * @param jobId 任务ID
     * @return 依赖的任务列表
     */
    List<SysJob> selectDependentJobs(@Param("jobId") Long jobId);

    /**
     * 导出定时任务列表
     *
     * @param request 查询参数
     * @return 导出定时任务列表
     */
    List<SysJob> exportJobList(SysJobQueryRequest request);

    /**
     * 批量更新任务执行时间
     * <p>
     * 用于批量更新多个任务的上次执行时间和下次执行时间
     * 提高批量更新的性能，避免逐个更新的开销
     * </p>
     *
     * @param jobs 需要更新的任务列表，包含 jobId、previousFireTime、nextFireTime
     * @return 更新的记录数
     */
    int batchUpdateExecutionTimes(@Param("jobs") List<SysJob> jobs);

    /**
     * 更新单个任务的执行时间
     * <p>
     * 专门用于更新任务的执行时间字段，避免更新其他不相关的字段
     * 提供更精确的更新控制
     * </p>
     *
     * @param jobId            任务ID
     * @param previousFireTime 上次执行时间
     * @param nextFireTime     下次执行时间
     * @return 更新的记录数
     */
    int updateJobExecutionTime(@Param("jobId") Long jobId,
                               @Param("previousFireTime") java.util.Date previousFireTime,
                               @Param("nextFireTime") java.util.Date nextFireTime);
}
