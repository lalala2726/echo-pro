package cn.zhangchuangla.storage.loader;

import cn.zhangchuangla.common.core.core.loader.DataLoader;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.core.service.StorageRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 统一存储配置加载器
 * 负责加载和初始化所有类型的存储配置
 *
 * @author Chuang
 */
@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageSystemProperties.class)
public class StorageConfigLoader implements DataLoader {

    private final StorageRegistryService storageRegistryService;

    @Override
    public String getName() {
        return "存储配置加载器";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean load() {
       return storageRegistryService.initializeStorage();
    }


}
