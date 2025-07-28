package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.model.entity.SysDictData;
import cn.zhangchuangla.system.model.request.dict.SysDictDataAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictDataQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictDataUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典数据 Service 接口
 *
 * @author Chuang
 */
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 分页查询字典数据列表
     *
     * @param dictType 字典类型
     * @param request  查询条件
     * @return 字典数据分页列表
     */
    Page<SysDictData> listDictData(String dictType, SysDictDataQueryRequest request);

    /**
     * 根据ID获取字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据信息
     */
    SysDictData getDictDataById(Long id);

    /**
     * 根据字典类型获取字典数据选项
     *
     * @param dictType 字典类型
     * @return 字典数据选项列表
     */
    List<Option<String>> getDictDataOption(String dictType);

    /**
     * 添加字典数据
     *
     * @param request 添加请求
     * @return 是否添加成功
     */
    boolean addDictData(SysDictDataAddRequest request);

    /**
     * 更新字典数据
     *
     * @param request 更新请求
     * @return 是否更新成功
     */
    boolean updateDictData(SysDictDataUpdateRequest request);

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID列表
     * @return 是否删除成功
     */
    boolean deleteDictData(List<Long> ids);

    /**
     * 根据字典类型删除字典数据
     *
     * @param dictTypes 字典类型列表
     */
    void deleteDictDataByDictType(List<String> dictTypes);

    /**
     * 检查字典数据值是否存在
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param dataId    字典数据ID（更新时排除自身）
     * @return 是否存在
     */
    boolean isDictDataExistByValue(String dictType, String dictValue, Long dataId);
}
