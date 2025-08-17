package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.system.core.mapper.SysOperationLogMapper;
import cn.zhangchuangla.system.core.model.entity.SysOperationLog;
import cn.zhangchuangla.system.core.model.request.log.SysOperationLogQueryRequest;
import cn.zhangchuangla.system.core.service.SysOperationLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志服务实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
    public Page<SysOperationLog> listOperationLog(SysOperationLogQueryRequest request) {
        Page<SysOperationLog> sysOperationLogPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysOperationLogMapper.listOperationLog(sysOperationLogPage, request);
    }


    /**
     * 清空操作日志
     *
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanOperationLog() {
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

    /**
     * 导出操作日志
     *
     * @param request 请求对象
     * @return 操作日志列表
     */
    @Override
    public List<SysOperationLog> exportOperationLog(SysOperationLogQueryRequest request) {
        return sysOperationLogMapper.listOperationLog(request);
    }

    /**
     * 删除操作日志
     *
     * @param ids 日志ID列表
     * @return 是否成功
     */
    @Override
    public boolean deleteOperationLogById(List<Long> ids) {
        Assert.isTrue(CollectionUtils.isNotEmpty(ids), "请选择要删除的登录日志");
        return removeByIds(ids);
    }

}




