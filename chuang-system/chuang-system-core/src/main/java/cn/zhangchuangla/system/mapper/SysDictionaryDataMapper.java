package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysDictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryDataRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface SysDictionaryDataMapper extends BaseMapper<SysDictionaryData> {


    /**
     * 根据字典名称获取字典值
     *
     * @param dictionaryName 字典名称
     * @return 字典值集合
     */
    List<SysDictionaryData> dictionaryDataService(@Param("dictionaryName") String dictionaryName);

    /**
     * 根据字典值id删除字典值,支持批量删除
     *
     * @param ids 字典值id
     */
    void deleteDictionaryItem(List<Long> ids);


    /**
     * 根据字典id获取字典值
     *
     * @param id      字典ID
     * @param request 字典值查询参数
     * @param page    分页
     * @return 字典值集合
     */
    Page<SysDictionaryData> getDictDataByDictionaryName(Page<SysDictionaryData> page,
                                                        @Param("dictionaryId") Long id,
                                                        @Param("request") DictionaryDataRequest request);

    /**
     * 修改字典值,通过所属的dictName进行修改
     *
     * @param request 请求参数
     * @return 操作结果
     */
    int updateDictionaryDataByDictName(@Param("request") SysDictionaryData request, @Param("dictName") String dictName);
}




