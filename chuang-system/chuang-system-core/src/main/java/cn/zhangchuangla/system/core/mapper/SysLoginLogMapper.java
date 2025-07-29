package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysLoginLog;
import cn.zhangchuangla.system.core.model.request.log.SysLoginLogQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 分页查询登录日志
     *
     * @param sysLoginLogPage 分页对象
     * @return 返回分页数据
     */
    Page<SysLoginLog> listLoginLog(Page<SysLoginLog> sysLoginLogPage, @Param("request") SysLoginLogQueryRequest request);

    /**
     * 查询登录日志
     *
     * @param request 查询参数
     * @return 登录日志列表
     */
    List<SysLoginLog> listLoginLog(@Param("request") SysLoginLogQueryRequest request);

    /**
     * 清空登录日志
     */
    void cleanLoginLog();
}




