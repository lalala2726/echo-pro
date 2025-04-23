package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictUpdateRequest;
import cn.zhangchuangla.system.model.vo.dict.SysDictItemOptionVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictItemVo;
import cn.zhangchuangla.system.model.vo.dict.SysDictVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 13:32
 */
@Mapper(componentModel = "spring")
public interface SysDictConverter {

    /**
     * 将字典添加请求类 转换为 字典 实体类
     *
     * @param request 字典添加请求参数
     * @return 字典 实体类
     */
    SysDict toEntity(SysDictAddRequest request);

    /**
     * 将字典更新请求类 转换为 字典 实体类
     *
     * @param request 字典更新请求参数
     * @return 字典 实体类
     */
    SysDict toEntity(SysDictUpdateRequest request);

    /**
     * 将字典项添加请求类 转换为 字典项 实体类
     *
     * @param request 字典项添加请求参数
     * @return 字典 实体类
     */
    SysDictItem toEntity(SysDictItemAddRequest request);

    /**
     * 将字典项更新请求类 转换为 字典项 实体类
     *
     * @param request 字典项更新请求参数
     * @return 字典 实体类
     */
    SysDictItem toEntity(SysDictItemUpdateRequest request);


    /**
     * 将 字典 实体类 转换为 字典 视图对象
     *
     * @param sysDict 字典 实体类
     * @return 字典 视图对象
     */
    SysDictVo toSysDictVo(SysDict sysDict);

    /**
     * 将 字典项 实体类 转换为 字典项 视图对象
     *
     * @param sysDictItem 字典项实体类
     * @return 字典项 视图对象
     */
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "remark", target = "remark")
    SysDictItemVo toSysDictItemVo(SysDictItem sysDictItem);

    /**
     * 将 字典 实体类 转换为 字典 视图对象列表
     *
     * @return 字典 视图对象列表
     */
    List<SysDictItemOptionVo> toSysDictItemOptionVo(List<SysDictItem> sysDictItems);

}
