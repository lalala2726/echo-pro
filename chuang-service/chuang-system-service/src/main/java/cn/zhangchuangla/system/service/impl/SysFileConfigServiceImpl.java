package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.mapper.SysFileConfigMapper;
import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.model.request.file.SysFileConfigAddRequest;
import cn.zhangchuangla.system.model.request.file.SysFileConfigListRequest;
import cn.zhangchuangla.system.model.request.file.SysFileConfigUpdateRequest;
import cn.zhangchuangla.system.service.SysFileConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 文件配置服务实现类
 *
 * @author zhangchuang
 */
@Service
public class SysFileConfigServiceImpl extends ServiceImpl<SysFileConfigMapper, SysFileConfig>
        implements SysFileConfigService {

    private final SysFileConfigMapper sysFileConfigMapper;

    public SysFileConfigServiceImpl(SysFileConfigMapper sysFileConfigMapper) {
        this.sysFileConfigMapper = sysFileConfigMapper;
    }

    /**
     * 查询文件配置列表
     *
     * @param request 查询参数
     * @return 文件配置列表
     */
    @Override
    public Page<SysFileConfig> listSysFileConfig(SysFileConfigListRequest request) {
        Page<SysFileConfig> sysFileConfigPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileConfigMapper.listSysFileConfig(sysFileConfigPage, request);
    }

    /**
     * 根据id获取文件配置信息
     *
     * @param id 文件配置id
     * @return 文件配置信息
     */
    @Override
    public SysFileConfig getSysFileConfigById(Integer id) {
        return getById(id);
    }

    /**
     * 根据id删除文件配置信息
     *
     * @param id 文件配置id
     * @return 操作结果
     */
    @Override
    public boolean deleteSysFileConfigById(Integer id) {
        if (isMaster(id)) {
            throw new ServiceException("主配置不允许删除");
        }
        return removeById(id);
    }

    /**
     * 添加文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveFileConfig(SysFileConfigAddRequest request) {
        if (isStorageKeyExist(request.getStorageKey())) {
            throw new ServiceException(String.format("存储key【%s】已存在", request.getStorageKey()));
        }
        SysFileConfig sysFileConfig = new SysFileConfig();
        BeanUtils.copyProperties(request, sysFileConfig);
        return save(sysFileConfig);
    }

    /**
     * 更新文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateFileConfigById(SysFileConfigUpdateRequest request) {
        if (isStorageKeyExist(request.getStorageKey())) {
            throw new ServiceException(String.format("存储key【%s】已存在", request.getStorageKey()));
        }
        SysFileConfig sysFileConfig = new SysFileConfig();
        BeanUtils.copyProperties(request, sysFileConfig);
        return updateById(sysFileConfig);
    }


    /**
     * 判断存储key是否存在
     *
     * @param storageKey 存储key名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isStorageKeyExist(String storageKey) {
        LambdaQueryWrapper<SysFileConfig> eq = new LambdaQueryWrapper<SysFileConfig>()
                .eq(SysFileConfig::getStorageKey, storageKey);
        return count(eq) > 0;
    }

    /**
     * 判断是否主配置
     *
     * @param id 文件配置id
     * @return true是主配置，false不是主配置
     */
    @Override
    public boolean isMaster(Integer id) {
        LambdaQueryWrapper<SysFileConfig> eq = new LambdaQueryWrapper<SysFileConfig>()
                .eq(SysFileConfig::getId, id)
                .eq(SysFileConfig::getIsMaster, Constants.IS_FILE_UPLOAD_MASTER);
        return count(eq) > 0;
    }

    /**
     * 读取主配置
     *
     * @return 主配置
     */
    @Override
    public SysFileConfig getMasterConfig() {
        LambdaQueryWrapper<SysFileConfig> eq = new LambdaQueryWrapper<SysFileConfig>()
                .eq(SysFileConfig::getIsMaster, Constants.IS_FILE_UPLOAD_MASTER);
        return getOne(eq);
    }
}




