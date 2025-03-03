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

    Page<Dictionary> getDictionaryList(Page<Dictionary> dictionaryPage, @Param("request") DictionaryRequest request);
}




