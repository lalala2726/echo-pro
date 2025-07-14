package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.model.entity.SysDictKey;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysDictKeyService extends IService<SysDictKey> {


    /**
     * 获取字典键列表
     *
     * @param page    分页
     * @param request 请求参数
     * @return 分页结果
     */
    Page<SysDictKey> listDictKey(Page<SysDictKey> page, SysDictKeyQueryRequest request);

    /**
     * 根据id获取字典键
     *
     * @param id id
     * @return 字典键
     */
    SysDictKey getDictKeyById(Long id);

    /**
     * 添加字典键
     *
     * @param request 请求参数
     * @return 是否添加成功
     */
    boolean addDictKey(SysDictKeyAddRequest request);

    /**
     * 修改字典键
     *
     * @param request 请求参数
     * @return 是否修改成功
     */
    boolean updateDictKey(SysDictKeyUpdateRequest request);

    /**
     * 删除字典键
     *
     * @param id 字典ID
     * @return 是否删除成功
     */
    boolean deleteDictKey(List<Long> id);


    /**
     * 检查字典键编码是否存在
     *
     * @param dictKey 字典键
     * @return true 存在，false 不存在
     */
    boolean isDictKeyExist(String dictKey);


    /**
     * 刷新字典缓存
     *
     * @return 操作结果
     */
    boolean refreshCache();

    /**
     * 获取所有字典键
     *
     * @return 字典键列表
     */
    List<Option<String>> getAllDictKey();
}
