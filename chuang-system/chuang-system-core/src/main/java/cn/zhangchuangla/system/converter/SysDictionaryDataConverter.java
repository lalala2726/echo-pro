package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysDictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryDataRequest;
import cn.zhangchuangla.system.model.vo.dictionary.DictionaryDataVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:33
 */
@Mapper(componentModel = "spring")
public interface SysDictionaryDataConverter {

    /**
     * 将新增字典项请求转换为实体类
     *
     * @param request 新增字典项请求
     * @return 字典项实体类
     */
    SysDictionaryData toEntity(AddDictionaryDataRequest request);

    /**
     * 将更新字典项请求转换为实体类
     *
     * @param request 更新字典项请求
     * @return 字典项实体类
     */
    SysDictionaryData toEntity(UpdateDictionaryDataRequest request);


    /**
     * 将新增字典请求转换为实体类
     *
     * @param request 新增字典请求
     * @return 字典实体类
     */
    SysDictionaryData toEntity(AddDictionaryRequest request);

    /**
     * 将字典项实体类转换为字典项视图对象
     *
     * @param sysDictionaryData 字典项实体类
     * @return 字典项视图对象
     */
    DictionaryDataVo toDictionaryDataVo(SysDictionaryData sysDictionaryData);
}
