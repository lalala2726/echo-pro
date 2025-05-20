package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysConfig;
import cn.zhangchuangla.system.model.request.config.SysConfigAddRequest;
import cn.zhangchuangla.system.model.request.config.SysConfigQueryRequest;
import cn.zhangchuangla.system.model.request.config.SysConfigUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统配置接口
 *
 * @author Chuang
 */
public interface SysConfigService extends IService<SysConfig> {


    /**
     * 分页查询系统配置信息
     *
     * @param request 查询参数
     * @return 分页数据
     */
    Page<SysConfig> listSysConfig(SysConfigQueryRequest request);


    /**
     * 根据id查询系统配置信息
     *
     * @param id 系统配置id
     * @return 系统配置信息
     */
    SysConfig getSysConfigById(Integer id);


    /**
     * 保存系统配置信息
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean saveSysConfig(SysConfigAddRequest request);

    /**
     * 根据id修改系统配置信息
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updateSysConfigById(SysConfigUpdateRequest request);

    /**
     * 根据id删除系统配置信息，支持批量删除
     *
     * @param id 系统配置id集合
     * @return 操作结果
     */
    boolean deleteSysConfigById(List<Integer> id);


    /**
     * 判断配置键名是否存在
     *
     * @param configKey 配置键名
     * @return true 存在，false不存在
     */
    boolean isConfigKeyExist(String configKey);


}
