package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.DictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryDataRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface DictionaryItemMapper extends BaseMapper<DictionaryData> {

    Page<DictionaryData> dictionaryItemList(Page<DictionaryData> page, @Param("request") DictionaryDataRequest request);

    List<DictionaryData> dictionaryDataService(@Param("dictionaryName") String dictionaryName);

    void deleteDictionaryItem(List<Long> ids);
}




