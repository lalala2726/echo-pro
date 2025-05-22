package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.system.mapper.SysConfigMapper;
import cn.zhangchuangla.system.model.entity.SysConfig;
import cn.zhangchuangla.system.model.request.config.SysConfigAddRequest;
import cn.zhangchuangla.system.model.request.config.SysConfigQueryRequest;
import cn.zhangchuangla.system.model.request.config.SysConfigUpdateRequest;
import cn.zhangchuangla.system.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务接口实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig>
        implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;


    /**
     * 分页查询系统配置
     *
     * @param request 查询参数
     * @return 分页数据
     */
    @Override
    public Page<SysConfig> listSysConfig(SysConfigQueryRequest request) {
        Page<SysConfig> sysConfigPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysConfigMapper.listSysConfig(sysConfigPage, request);
    }

    /**
     * 根据id查询系统配置
     *
     * @param id 系统配置id
     * @return 系统配置
     */
    @Override
    public SysConfig getSysConfigById(Integer id) {
        return getById(id);
    }

    /**
     * 新增系统配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveSysConfig(SysConfigAddRequest request) {
        if (isConfigKeyExist(request.getConfigKey())) {
            throw new ServiceException(String.format("参数键名【%s】已存在", request.getConfigKey()));
        }
        SysConfig sysConfig = new SysConfig();
        BeanUtils.copyProperties(request, sysConfig);
        return save(sysConfig);
    }

    /**
     * 修改系统配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateSysConfigById(SysConfigUpdateRequest request) {
        if (isConfigKeyExist(request.getConfigKey())) {
            throw new ServiceException(String.format("参数键名【%s】已存在", request.getConfigKey()));
        }
        SysConfig sysConfig = new SysConfig();
        BeanUtils.copyProperties(request, sysConfig);
        return updateById(sysConfig);
    }

    /**
     * 删除系统配置
     *
     * @param id 系统配置id集合
     * @return 操作结果
     */
    @Transactional
    @Override
    public boolean deleteSysConfigById(List<Integer> id) {
        if (id != null && !id.isEmpty()) {
            return removeByIds(id);
        }
        return false;
    }

    /**
     * 判断配置键名是否存在
     *
     * @param configKey 配置键名
     * @return true存在，false不存在
     */
    @Override
    public boolean isConfigKeyExist(String configKey) {
        LambdaQueryWrapper<SysConfig> eq = new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey);
        return count(eq) > 0;
    }
}




