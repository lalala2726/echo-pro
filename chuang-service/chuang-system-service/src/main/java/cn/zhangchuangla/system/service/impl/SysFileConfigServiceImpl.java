package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.mapper.SysFileConfigMapper;
import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.model.request.file.SysFileConfigAddRequest;
import cn.zhangchuangla.system.service.SysFileConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
}




