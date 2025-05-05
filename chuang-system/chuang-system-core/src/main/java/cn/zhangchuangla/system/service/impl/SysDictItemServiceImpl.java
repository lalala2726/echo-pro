package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.converter.SysDictConverter;
import cn.zhangchuangla.system.mapper.SysDictItemMapper;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemListRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import cn.zhangchuangla.system.service.SysDictItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典项 服务实现层
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem>
        implements SysDictItemService {

    private final SysDictItemMapper sysDictItemMapper;
    private final SysDictConverter sysDictConverter;

    /**
     * 获取字典项列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    @Override
    public Page<SysDictItem> listDictItem(Page<SysDictItem> page, String dictType, SysDictItemListRequest request) {
        return sysDictItemMapper.listDictItem(page, dictType, request);
    }

    /**
     * 根据id获取字典项
     *
     * @param id id
     * @return 字典项
     */
    @Override
    public SysDictItem getDictItemById(Long id) {
        return sysDictItemMapper.selectById(id);
    }

    /**
     * 添加字典项
     *
     * @param request 请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictItem(SysDictItemAddRequest request) {
        // 检查同一字典类型下字典项值是否重复
        if (isDictItemValueExist(request.getDictType(), request.getItemValue(), null)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "同一字典类型下字典项值不能重复: " + request.getItemValue());
        }

        SysDictItem sysDictItem = sysDictConverter.toEntity(request);
        sysDictItem.setCreateBy(SecurityUtils.getUsername());
        return save(sysDictItem);
    }

    /**
     * 更新字典项
     *
     * @param request 请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateDictItem(SysDictItemUpdateRequest request) {
        // 检查字典项是否存在
        SysDictItem existDictItem = sysDictItemMapper.selectById(request.getId());
        if (existDictItem == null) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典项不存在");
        }

        // 检查同一字典类型下字典项值是否重复 (排除自身)
        if (isDictItemValueExist(request.getDictType(), request.getItemValue(), request.getId())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "同一字典类型下字典项值不能重复: " + request.getItemValue());
        }

        SysDictItem sysDictItem = sysDictConverter.toEntity(request);
        sysDictItem.setUpdateBy(SecurityUtils.getUsername());
        return sysDictItemMapper.updateById(sysDictItem) > 0;
    }

    /**
     * 删除字典项
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDictItem(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return sysDictItemMapper.deleteByIds(ids) > 0;
    }

    /**
     * 根据字典类型编码删除字典项
     *
     * @param dictTypes 字典类型编码列表
     */
    @Override
    public void deleteDictItemByDictType(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SysDictItem> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysDictItem::getDictType, dictTypes);
        // 返回删除的记录数是否大于0
        sysDictItemMapper.delete(queryWrapper);
    }

    /**
     * 根据旧的字典类型编码更新为新的字典类型编码
     *
     * @param oldDictType 旧字典类型编码
     * @param newDictType 新字典类型编码
     */
    @Override
    public void updateDictItemDictType(String oldDictType, String newDictType) {
        if (StringUtils.isAnyBlank(oldDictType, newDictType) || oldDictType.equals(newDictType)) {
            return;
        }
        LambdaUpdateWrapper<SysDictItem> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(SysDictItem::getDictType, oldDictType)
                .set(SysDictItem::getDictType, newDictType);

        // 返回影响的行数是否大于0
        update(updateWrapper);
    }

    /**
     * 检查同一字典类型下字典项值是否重复
     *
     * @param dictType  字典类型编码
     * @param itemValue 字典项值
     * @param itemId    字典项ID (更新时排除自身)
     * @return true 重复, false 不重复
     */
    @Override
    public boolean isDictItemValueExist(String dictType, String itemValue, Long itemId) {
        if (StringUtils.isAnyBlank(dictType, itemValue)) {
            // 关键参数为空，无法判断，或者认为不重复
            return false;
        }
        LambdaQueryWrapper<SysDictItem> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictItem::getDictType, dictType)
                .eq(SysDictItem::getItemValue, itemValue);
        // 如果是更新操作，排除当前项自身
        if (itemId != null) {
            queryWrapper.ne(SysDictItem::getId, itemId);
        }
        return sysDictItemMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 根据字典类型编码获取字典项列表
     *
     * @param dictType 字典类型编码
     * @return 字典项列表
     */
    @Override
    public List<Option<String>> getDictItemOption(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return List.of();
        }
        LambdaQueryWrapper<SysDictItem> eq = new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictType, dictType);
        List<SysDictItem> list = list(eq);
        return list.stream().map(item ->
                new Option<>(item.getItemValue(), item.getItemLabel())
        ).toList();
    }
}




