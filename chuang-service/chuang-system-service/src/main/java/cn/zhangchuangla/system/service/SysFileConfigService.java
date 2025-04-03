package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.model.request.file.SysFileConfigAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件配置接口
 *
 * @author zhangchuang
 */
public interface SysFileConfigService extends IService<SysFileConfig> {


    /**
     * 新增文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean saveFileConfig(SysFileConfigAddRequest request);

    /**
     * 判断存储key是否存在
     *
     * @param storageKey 存储key
     * @return true 存在，false不存在
     */
    boolean isStorageKeyExist(String storageKey);
}
