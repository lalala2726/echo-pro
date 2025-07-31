package cn.zhangchuangla.quartz.service;

import cn.zhangchuangla.quartz.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobLogQueryRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobLogVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * 定时任务日志服务接口
 *
 * @author Chuang
 */
public interface SysJobLogService extends IService<SysJobLog> {

    /**
     * 分页查询定时任务日志列表
     *
     * @param request 查询条件
     * @return 定时任务日志分页列表
     */
    Page<SysJobLog> selectJobLogList(SysJobLogQueryRequest request);

    /**
     * 根据日志ID查询日志详情
     *
     * @param jobLogId 日志ID
     * @return 日志详情
     */
    SysJobLog selectJobLogById(Long jobLogId);

    /**
     * 根据任务ID查询日志列表
     *
     * @param jobId 任务ID
     * @return 日志列表
     */
    @Deprecated
    List<SysJobLogVo> selectLogsByJobId(Long jobId);

    /**
     * 添加任务日志
     *
     * @param jobLog 任务日志
     * @return 操作结果
     */
    boolean addJobLog(SysJobLog jobLog);

    /**
     * 删除任务日志
     *
     * @param logIds 日志ID列表
     * @return 操作结果
     */
    boolean deleteJobLogs(List<Long> logIds);

    /**
     * 清理指定日期之前的日志
     *
     * @param beforeDate 指定日期
     * @return 清理数量
     */
    int cleanLogsBefore(Date beforeDate);

    /**
     * 清理所有日志
     *
     * @return 清理数量
     */
    int cleanAllLogs();

    /**
     * 获取任务执行统计
     *
     * @param jobId     任务ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计结果
     */
    List<SysJobLogVo> getJobStatistics(Long jobId, Date startDate, Date endDate);

    /**
     * 获取最近的执行日志
     *
     * @param jobId 任务ID
     * @param limit 限制数量
     * @return 日志列表
     */
    List<SysJobLogVo> getRecentLogs(Long jobId, Integer limit);

    /**
     * 记录任务执行开始
     *
     * @param job 任务信息
     * @return 日志ID
     */
    Long recordJobStart(SysJobLog job);

    /**
     * 记录任务执行结束
     *
     * @param jobLogId      日志ID
     * @param status        执行状态
     * @param message       执行消息
     * @param exceptionInfo 异常信息
     */
    void recordJobEnd(Long jobLogId, Integer status, String message, String exceptionInfo);

    /**
     * 导出任务日志列表
     *
     * @param request 查询条件
     * @return 日志列表
     */
    List<SysJobLog> exportJobLogList(SysJobLogQueryRequest request);
}
