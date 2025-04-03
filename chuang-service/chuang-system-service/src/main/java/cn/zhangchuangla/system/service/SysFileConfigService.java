package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.model.request.file.SysFileConfigAddRequest;
import cn.zhangchuangla.system.model.request.file.SysFileConfigListRequest;
import cn.zhangchuangla.system.model.request.file.SysFileConfigUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件配置接口
 *
 * @author zhangchuang
 */
public interface SysFileConfigService extends IService<SysFileConfig> {


    /**
     * 文件配置列表
     *
     * @param request 查询参数
     * @return 返回文件配置列表
     */
    Page<SysFileConfig> listSysFileConfig(SysFileConfigListRequest request);


    /**
     * 根据id查询文件配置
     *
     * @param id 文件配置id
     * @return 文件配置对象
     */
    SysFileConfig getSysFileConfigById(Integer id);


    /**
     * 根据id删除文件配置
     *
     * @param id 文件配置id
     * @return 操作结果
     */
    boolean deleteSysFileConfigById(Integer id);


    /**
     * 新增文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean saveFileConfig(SysFileConfigAddRequest request);


    /**
     * 修改文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updateFileConfigById(SysFileConfigUpdateRequest request);

    /**
     * 判断存储key是否存在
     *
     * @param storageKey 存储key
     * @return true 存在，false不存在
     */
    boolean isStorageKeyExist(String storageKey);


    /**
     * 判断是否是主配置
     *
     * @param id 文件配置id
     * @return true 是主配置，false不是主配置
     */
    boolean isMaster(Integer id);

    /**
     * 读取主配置
     *
     * @return 返回主配置
     */
    SysFileConfig getMasterConfig();
}
