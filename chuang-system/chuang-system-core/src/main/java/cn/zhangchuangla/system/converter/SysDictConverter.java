package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeUpdateRequest;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 13:32
 */
@Mapper(componentModel = "spring")
public interface SysDictConverter {


    /**
     * 将 更新请求对象 转换为 实体对象
     *
     * @param request 请求对象
     * @return 实体对象
     */
    SysDictType toEntity(SysDictTypeUpdateRequest request);

    /**
     * 将 添加请求对象 转换为 实体对象
     *
     * @param request 请求对象
     * @return 实体对象
     */
    SysDictType toEntity(SysDictTypeAddRequest request);


    SysDictItem toEntity(SysDictItemAddRequest request);


    SysDictItem toEntity(SysDictItemUpdateRequest request);
}
