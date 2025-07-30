package cn.zhangchuangla.quartz.mapper;

import cn.zhangchuangla.quartz.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobLogQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 定时任务日志 Mapper 接口
 *
 * @author Chuang
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 分页查询定时任务日志列表
     *
     * @param page    分页对象
     * @param request 查询条件
     * @return 定时任务日志分页列表
     */
    Page<SysJobLog> selectJobLogList(Page<SysJobLog> page, @Param("request") SysJobLogQueryRequest request);

    /**
     * 根据任务ID查询日志列表
     *
     * @param jobId 任务ID
     * @return 日志列表
     */
    List<SysJobLog> selectLogsByJobId(@Param("jobId") Long jobId);

    /**
     * 清理指定日期之前的日志
     *
     * @param beforeDate 指定日期
     * @return 清理数量
     */
    int cleanLogsBefore(@Param("beforeDate") Date beforeDate);

    /**
     * 批量删除日志
     *
     * @param logIds 日志ID列表
     * @return 删除数量
     */
    int batchDeleteLogs(@Param("logIds") List<Long> logIds);

    /**
     * 统计任务执行情况
     *
     * @param jobId     任务ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计结果
     */
    List<SysJobLog> selectJobStatistics(@Param("jobId") Long jobId,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate);

    /**
     * 获取最近的执行日志
     *
     * @param jobId 任务ID
     * @param limit 限制数量
     * @return 日志列表
     */
    List<SysJobLog> selectRecentLogs(@Param("jobId") Long jobId, @Param("limit") Integer limit);

    /**
     * 导出定时任务日志列表
     *
     * @param request 查询条件
     * @return 定时任务日志列表
     */
    List<SysJobLog> exportJobLogList(@Param("request") SysJobLogQueryRequest request);
}
