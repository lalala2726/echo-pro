package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysConfig;
import cn.zhangchuangla.system.model.request.config.SysConfigAddRequest;
import cn.zhangchuangla.system.model.request.config.SysConfigUpdateRequest;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:37
 */
@Mapper(componentModel = "spring")
public interface SysConfigConverter {

    /**
     * 将系统配置添加请求类 转换为 SysConfig 实体类
     *
     * @param request 系统配置添加请求参数
     * @return SysConfig 实体类
     */
    SysConfig toEntity(SysConfigAddRequest request);

    /**
     * 将系统配置更新请求类 转换为 SysConfig 实体类
     *
     * @param request 系统配置更新请求参数
     * @return SysConfig 实体类
     */
    SysConfig toEntity(SysConfigUpdateRequest request);
}
