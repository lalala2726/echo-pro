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
     * 字典类型添加请求转换为字典类型实体
     *
     * @param request 字典类型添加请求
     * @return 字典类型实体
     */
    SysDictType toSysDictType(SysDictTypeAddRequest request);

    /**
     * 字典项更新请求转换为字典项实体
     *
     * @param request 字典项更新请求
     * @return 字典项实体
     */
    SysDictType toSysDictType(SysDictTypeUpdateRequest request);

    /**
     * 字典项更新请求转换为字典项实体
     *
     * @param request 字典项更新请求
     * @return 字典项实体
     */
    SysDictItem toSysDictItem(SysDictItemUpdateRequest request);

    /**
     * 字典项添加请求转换为字典项实体
     *
     * @param request 字典项添加请求
     * @return 字典项实体
     */
    SysDictItem toSysDictItem(SysDictItemAddRequest request);
}
