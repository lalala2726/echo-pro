package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogVo;
import cn.zhangchuangla.system.model.vo.log.SysOperationLogVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 18:46
 */
@Mapper(componentModel = "spring")
public interface SysLogConverter {

    /**
     * 将登录日志转换为登录日志视图对象
     *
     * @param sysLoginLog 登录日志
     * @return 登录日志视图对象
     */
    SysLoginLogVo toSysLoginLogVo(SysLoginLog sysLoginLog);

    /**
     * 将操作日志转换为操作日志视图对象
     *
     * @param sysOperationLog 操作日志
     * @return 操作日志视图对象
     */
    SysOperationLogVo toSysOperationLogVo(SysOperationLog sysOperationLog);
}
