package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.DictionaryItemMapper;
import cn.zhangchuangla.system.model.entity.DictionaryItem;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryItemRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryItemRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryItemRequest;
import cn.zhangchuangla.system.service.DictionaryItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
public class DictionaryItemServiceImpl extends ServiceImpl<DictionaryItemMapper, DictionaryItem>
        implements DictionaryItemService {

    private final DictionaryItemMapper dictionaryItemMapper;

    public DictionaryItemServiceImpl(DictionaryItemMapper dictionaryItemMapper) {
        this.dictionaryItemMapper = dictionaryItemMapper;
    }

    @Override
    public boolean noDuplicateKeys(String itemKey) {
        ParamsUtils.paramsNotIsNullOrBlank("字典项键不能为空", itemKey);
        LambdaQueryWrapper<DictionaryItem> queryWrapper = new LambdaQueryWrapper<DictionaryItem>()
                .eq(DictionaryItem::getItemKey, itemKey);
        DictionaryItem dictionaryItem = getOne(queryWrapper);
        LambdaQueryWrapper<DictionaryItem> eq = new LambdaQueryWrapper<DictionaryItem>()
                .eq(DictionaryItem::getDictionaryId, dictionaryItem.getDictionaryId())
                .eq(DictionaryItem::getItemKey, itemKey);
        return count(eq) > 0;
    }

    @Override
    public long getCountByDictionaryId(Long dictionaryId) {
        ParamsUtils.minValidParam(dictionaryId, "字典ID不能小于等于零!");
        LambdaQueryWrapper<DictionaryItem> eq = new LambdaQueryWrapper<DictionaryItem>().eq(DictionaryItem::getDictionaryId, dictionaryId);
        return count(eq);
    }

    @Override
    public Page<DictionaryItem> dictionaryItemList(DictionaryItemRequest request) {
        Page<DictionaryItem> page = new Page<>(request.getPageNum(), request.getPageSize());
        return dictionaryItemMapper.dictionaryItemList(page, request);
    }

    @Override
    public List<DictionaryItem> getDictionaryItemByIdDictName(String dictionaryName) {
        ParamsUtils.paramsNotIsNullOrBlank("字典名称不能为空", dictionaryName);
        return dictionaryItemMapper.dictionaryItemService(dictionaryName);
    }

    @Override
    public boolean addDictionaryItem(AddDictionaryItemRequest request) {
        DictionaryItem dictionaryItem = new DictionaryItem();
        BeanUtils.copyProperties(request, dictionaryItem);
        return save(dictionaryItem);
    }

    @Override
    public DictionaryItem getDictionaryById(Long id) {
        ParamsUtils.minValidParam(id, "字典ID不能小于等于零!");
        LambdaQueryWrapper<DictionaryItem> eq = new LambdaQueryWrapper<DictionaryItem>().eq(DictionaryItem::getId, id);
        return getOne(eq);
    }

    @Override
    public boolean updateDictionaryItem(UpdateDictionaryItemRequest request) {
        LambdaQueryWrapper<DictionaryItem> eq = new LambdaQueryWrapper<DictionaryItem>().eq(DictionaryItem::getId, request.getId());
        return update(eq);
    }

    @Override
    public void deleteDictionaryItem(List<Long> ids) {
        dictionaryItemMapper.deleteDictionaryItem(ids);
    }
}




