package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.system.core.model.entity.SysOperationLog;
import cn.zhangchuangla.system.core.model.request.log.SysOperationLogQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统操作日志接口
 *
 * @author Chuang
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 获取系统操作日志列表
     *
     * @param request 请求对象
     * @return 操作日志列表
     */
    Page<SysOperationLog> listOperationLog(SysOperationLogQueryRequest request);

    /**
     * 清空操作日志
     *
     * @return 是否成功
     */
    boolean cleanOperationLog();

    /**
     * 获取操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    SysOperationLog getOperationLogById(Long id);

    /**
     * 导出操作日志
     *
     * @param request 请求对象
     * @return 操作日志列表
     */
    List<SysOperationLog> exportOperationLog(SysOperationLogQueryRequest request);


    /**
     * 删除操作日志
     *
     * @param ids 日志ID列表
     * @return 是否成功
     */
    boolean deleteOperationLogById(List<Long> ids);
}
