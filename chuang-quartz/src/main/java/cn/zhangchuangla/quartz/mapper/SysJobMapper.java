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
     * 根据任务组查询任务列表
     *
     * @param jobGroup 任务组名
     * @return 任务列表
     */
    List<SysJob> selectJobsByGroup(@Param("jobGroup") String jobGroup);

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
}
