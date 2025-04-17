package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemListRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface SysDictItemService extends IService<SysDictItem> {

    /**
     * 获取指定字典编码下面的分页
     *
     * @param dictCode 字典编码
     * @param request  查询参数
     * @return 分页数据
     */
    Page<SysDictItem> listDictData(String dictCode, SysDictItemListRequest request);


    /**
     * 分页查询字典数据
     *
     * @param request 查询参数
     * @return 分页数据
     */
    Page<SysDictItem> listDictData(SysDictItemListRequest request);

    /**
     * 根据字典编码获取字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<SysDictItem> getDictItems(String dictCode);

    /**
     * 添加字典项值
     *
     * @param request 请求参数
     * @return 字典项
     */
    boolean addDictItem(SysDictItemAddRequest request);

    /**
     * 根据ID获取字典项
     *
     * @param id 字典项ID
     * @return 字典项
     */
    SysDictItem getDictItemById(Long id);

    /**
     * 修改字典项
     *
     * @param request 请求参数
     * @return 字典项
     */
    boolean updateDictItem(SysDictItemUpdateRequest request);

    /**
     * 删除字典项,支持批量删除
     *
     * @param ids 字典项ID
     * @return 是否删除成功
     */
    boolean deleteDictItem(List<Long> ids);
}
