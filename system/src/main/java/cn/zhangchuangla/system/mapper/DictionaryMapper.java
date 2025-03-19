package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface DictionaryMapper extends BaseMapper<Dictionary> {

    /**
     * 分页查询字典
     *
     * @param dictionaryPage 分页对象
     * @param request        请求参数
     * @return 返回字典分页数据
     */
    Page<Dictionary> getDictionaryList(Page<Dictionary> dictionaryPage, @Param("request") DictionaryRequest request);

    /**
     * 根据id查询字典数量,排除当前id
     *
     * @param id 排除的ID
     * @return 返回数量
     */
    Long getDictionaryCountExcludeCurrentId(@Param("id") Long id, @Param("dictName") String dictName);
}




