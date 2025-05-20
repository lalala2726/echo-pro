package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.SysDictTypeMapper;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeUpdateRequest;
import cn.zhangchuangla.system.service.SysDictItemService;
import cn.zhangchuangla.system.service.SysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictItemService sysDictItemService;

    /**
     * 获取字典类型列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    @Override
    public Page<SysDictType> listDictType(Page<SysDictType> page, SysDictTypeQueryRequest request) {
        return dictTypeMapper.listDictType(page, request);
    }

    /**
     * 根据id获取字典类型
     *
     * @param id id
     * @return 字典类型
     */
    @Override
    public SysDictType getDictTypeById(Long id) {
        return getById(id);
    }

    /**
     * 添加字典类型
     *
     * @param request 请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictType(SysDictTypeAddRequest request) {
        if (isDictTypeExist(request.getDictType())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典类型已存在: " + request.getDictType());
        }
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictType.setCreateBy(SecurityUtils.getUsername());
        return save(sysDictType);
    }

    /**
     * 更新字典类型
     *
     * @param request 请求
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictTypeUpdateRequest request) {
        LambdaQueryWrapper<SysDictType> ne = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, request.getDictType())
                .ne(SysDictType::getId, request.getId());
        if (count(ne) > 0) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典类型已存在: " + request.getDictType());
        }
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictType.setUpdateBy(SecurityUtils.getUsername());
        return updateById(sysDictType);
    }

    /**
     * 删除字典类型
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        List<SysDictType> sysDictTypes = listByIds(ids);
        if (sysDictTypes.isEmpty()) {
            return false;
        }

        List<String> dictTypes = sysDictTypes.stream()
                .map(SysDictType::getDictType)
                .distinct()
                .toList();

        if (!dictTypes.isEmpty()) {
            sysDictItemService.deleteDictItemByDictType(dictTypes);
        }

        return removeByIds(ids);
    }

    /**
     * 判断字典类型是否存在
     *
     * @param dictType 字典类型
     * @return true存在，false不存在
     */
    @Override
    public boolean isDictTypeExist(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return false;
        }
        LambdaQueryWrapper<SysDictType> eq = new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dictType);
        return count(eq) > 0;
    }

}




