package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysDictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryRequest;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:33
 */
@Mapper(componentModel = "spring")
public interface SysDictionaryConverter {

    /**
     * 将新增字典请求转换为实体类
     *
     * @param request 新增字典请求
     * @return 字典实体类
     */
    SysDictionary toEntity(AddDictionaryRequest request);

    /**
     * 将更新字典请求转换为实体类
     *
     * @param request 更新字典请求
     * @return 字典实体类
     */
    SysDictionary toEntity(UpdateDictionaryRequest request);

    /**
     * 将字典实体类转换为字典视图对象
     *
     * @param sysDictionary 字典实体类
     * @return 字典视图对象
     */
    DictionaryVo toDictionaryVo(SysDictionary sysDictionary);
}
