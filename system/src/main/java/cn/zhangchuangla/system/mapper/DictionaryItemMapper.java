package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.DictionaryItem;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryItemRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface DictionaryItemMapper extends BaseMapper<DictionaryItem> {

    Page<DictionaryItem> dictionaryItemList(Page<DictionaryItem> page, @Param("request") DictionaryItemRequest request);

    List<DictionaryItem> dictionaryItemService(@Param("dictionaryName") String dictionaryName);

    void deleteDictionaryItem(List<Long> ids);
}




