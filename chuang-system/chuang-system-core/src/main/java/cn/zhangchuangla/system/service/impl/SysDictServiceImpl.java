package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.converter.SysDictConverter;
import cn.zhangchuangla.system.mapper.SysDictMapper;
import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.request.dict.SysDictAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictListRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictUpdateRequest;
import cn.zhangchuangla.system.service.SysDictItemService;
import cn.zhangchuangla.system.service.SysDictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict>
        implements SysDictService {

    private final SysDictMapper sysDictMapper;
    private final SysDictItemService sysDictItemService;
    private final SysDictConverter sysDictConverter;


    /**
     * 查询字典列表
     *
     * @param request 查询参数
     * @return 分页参数
     */
    @Override
    public Page<SysDict> listDict(SysDictListRequest request) {
        Page<SysDict> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictMapper.listDict(page, request);
    }

    /**
     * 添加字典
     *
     * @param request 请求参数
     * @return 添加结果
     */
    @Override
    public boolean addDict(SysDictAddRequest request) {
        SysDict sysDict = sysDictConverter.toEntity(request);
        return save(sysDict);
    }

    /**
     * 判断字典编码是否存在
     *
     * @param dictCode 字典编码
     * @return true 存在，false 不存在
     */
    @Override
    public boolean isDictCodeExist(String dictCode) {
        LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDict::getDictCode, dictCode);
        return count(queryWrapper) > 0;
    }

    /**
     * 判断字典名称是否存在
     *
     * @param name 字典名称
     * @return true 存在，false 不存在
     */
    @Override
    public boolean isDictNameExist(String name) {
        LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDict::getName, name);
        return count(queryWrapper) > 0;
    }

    /**
     * 根据字典ID获取字典信息
     *
     * @param id 字典ID
     * @return 字典信息
     */
    @Override
    public SysDict getDictById(Long id) {
        return getById(id);
    }

    /**
     * 删除字典，支持批量删除
     *
     * @param ids 字典ID集合
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDict(List<Long> ids) {
        //检查是否存在字典项
        ids.forEach(id -> {
            SysDict dictById = getDictById(id);
            boolean dictItemExistByDictCode = sysDictItemService.isDictItemExistByDictCode(dictById.getDictCode());
            if (dictItemExistByDictCode) {
                throw new ServiceException(String.format("字典: %s 存在字典项，不能删除", dictById.getName()));
            }
        });
        // 删除字典
        return removeByIds(ids);
    }

    /**
     * 更新字典
     *
     * @param request 请求参数
     * @return 更新结果
     */
    @Override
    public boolean updateDict(SysDictUpdateRequest request) {
        String dictCode = request.getDictCode();
        // 校验字典编码是否存在
        long count = this.count(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, dictCode)
                .ne(SysDict::getId, request.getId())
        );
        if (count > 0) {
            throw new ServiceException("字典编码已存在");
        }
        SysDict sysDict = sysDictConverter.toEntity(request);
        return updateById(sysDict);
    }
}




