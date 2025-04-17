package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.request.dict.SysDictAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictUpdateRequest;
import cn.zhangchuangla.system.model.vo.dict.SysDictVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 13:32
 */
@Mapper(componentModel = "spring")
public interface SysDictConverter {

    /**
     * 将字典添加请求类 转换为 SysDict 实体类
     *
     * @param request 字典添加请求参数
     * @return SysDict 实体类
     */
    SysDict toEntity(SysDictAddRequest request);

    /**
     * 将字典更新请求类 转换为 SysDict 实体类
     *
     * @param request 字典更新请求参数
     * @return SysDict 实体类
     */
    SysDict toEntity(SysDictUpdateRequest request);


    /**
     * 将 SysDict 实体类 转换为 SysDictVo 视图对象
     *
     * @param sysDict SysDict 实体类
     * @return SysDictVo 视图对象
     */
    SysDictVo toSysDictVo(SysDict sysDict);
}
