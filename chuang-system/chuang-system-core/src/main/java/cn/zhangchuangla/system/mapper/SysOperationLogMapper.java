package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.request.log.SysOperationLogQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    /**
     * 分页查询操作日志
     *
     * @param page    分页对象
     * @param request 查询参数
     * @return 返回分页数据
     */
    Page<SysOperationLog> listOperationLog(Page<SysOperationLog> page, @Param("request") SysOperationLogQueryRequest request);

    /**
     * 查询操作日志,无分页
     *
     * @param request 查询参数
     * @return 返回数据
     */
    List<SysOperationLog> listOperationLog(@Param("request") SysOperationLogQueryRequest request);

    /**
     * 清空操作日志
     */
    void cleanLoginLog();
}




