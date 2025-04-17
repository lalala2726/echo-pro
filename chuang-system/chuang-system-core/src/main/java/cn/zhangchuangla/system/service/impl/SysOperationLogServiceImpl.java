package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysOperationLogMapper;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.request.log.SysOperationLogListRequest;
import cn.zhangchuangla.system.service.SysOperationLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 *
 * @author zhangchuang
 */
@Service
@RequiredArgsConstructor
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog>
        implements SysOperationLogService {

    private final SysOperationLogMapper sysOperationLogMapper;


    /**
     * 获取系统操作日志列表
     *
     * @param request 请求对象
     * @return 操作日志列表
     */
    @Override
    public Page<SysOperationLog> listOperationLog(SysOperationLogListRequest request) {
        Page<SysOperationLog> sysOperationLogPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysOperationLogMapper.listOperationLog(sysOperationLogPage, request);
    }

    /**
     * 清空操作日志
     *
     * @return 是否成功
     */
    @Override
    public boolean cleanLoginLog() {
        sysOperationLogMapper.cleanLoginLog();
        return true;
    }

    /**
     * 获取操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    @Override
    public SysOperationLog getOperationLogById(Long id) {
        return getById(id);
    }
}




