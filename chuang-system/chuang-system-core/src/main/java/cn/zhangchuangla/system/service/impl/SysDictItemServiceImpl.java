package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.converter.SysDictConverter;
import cn.zhangchuangla.system.mapper.SysDictItemMapper;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemListRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import cn.zhangchuangla.system.service.SysDictItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem>
        implements SysDictItemService {

    private final SysDictItemMapper sysDictItemMapper;
    private final SysDictConverter sysDictConverter;

    /**
     * 分页查询字典数据
     *
     * @param dictCode 字典编码
     * @param request  查询参数
     * @return 分页数据
     */
    @Override
    public Page<SysDictItem> listDictData(String dictCode, SysDictItemListRequest request) {
        Page<SysDictItem> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictItemMapper.listSpecifyDictData(page, dictCode, request);
    }

    /**
     * 分页查询字典数据
     *
     * @param request 查询参数
     * @return 分页数据
     */
    @Override
    public Page<SysDictItem> listDictData(SysDictItemListRequest request) {
        Page<SysDictItem> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictItemMapper.listDictData(page, request);
    }

    /**
     * 根据字典编码获取字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @Override
    public List<SysDictItem> getDictItems(String dictCode) {
        LambdaQueryWrapper<SysDictItem> eq = new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictCode, dictCode);
        return list(eq);
    }

    /**
     * 添加字典项值
     *
     * @return 操作结果
     */
    @Override
    public boolean addDictItem(SysDictItemAddRequest request) {
        SysDictItem sysDictItem = sysDictConverter.toEntity(request);
        return save(sysDictItem);
    }

    /**
     * 根据ID获取字典项
     *
     * @param id 字典项ID
     * @return 字典项
     */
    @Override
    public SysDictItem getDictItemById(Long id) {
        return getById(id);
    }

    /**
     * 修改字典项
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateDictItem(SysDictItemUpdateRequest request) {
        SysDictItem entity = sysDictConverter.toEntity(request);
        return updateById(entity);
    }

    /**
     * 删除字典项，支持批量删除
     *
     * @param ids 字典项ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteDictItem(List<Long> ids) {
        return removeByIds(ids);
    }

    /**
     * 获取字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @Override
    public List<SysDictItem> getDictItemOptionVo(String dictCode) {
        LambdaQueryWrapper<SysDictItem> sysDictItemLambdaQueryWrapper = new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictCode, dictCode);
        return list(sysDictItemLambdaQueryWrapper);
    }
}




