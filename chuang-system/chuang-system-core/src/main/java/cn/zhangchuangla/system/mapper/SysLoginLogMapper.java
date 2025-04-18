package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 分页查询登录日志
     *
     * @param sysLoginLogPage 分页对象
     * @return 返回分页数据
     */
    Page<SysLoginLog> listLoginLog(Page<SysLoginLog> sysLoginLogPage, @Param("request") SysLoginLogListRequest request);

    /**
     * 清空登录日志
     */
    void cleanLoginLog();
}




