package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.request.log.SysOperationLogQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
