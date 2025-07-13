package cn.zhangchuangla.system.loader;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.loader.DataLoader;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.model.entity.SysDictKey;
import cn.zhangchuangla.system.model.entity.SysDictValue;
import cn.zhangchuangla.system.service.SysDictKeyService;
import cn.zhangchuangla.system.service.SysDictValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 字典加载器，在启动项目的时候统一将字典数据加载到缓存中
 * 本次操作为异步，所以不会影响启动速度
 *
 * @author Chuang
 * <p>
 * created on 2025/5/22 16:22
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SysDictLoader implements DataLoader {

    private final SysDictKeyService sysDictKeyService;
    private final SysDictValueService sysDictValueService;
    private final RedisCache redisCache;

    @Override
    public String getName() {
        return "系统字典加载器";
    }

    @Override
    public int getOrder() {
        // 优先级适中
        return 20;
    }

    @Override
    public boolean load() {
        try {
            log.info("开始加载系统字典数据到缓存...");

            // 1. 查询所有启用的字典类型
            LambdaQueryWrapper<SysDictKey> dictKeyLambdaQueryWrapper = new LambdaQueryWrapper<SysDictKey>()
                    .eq(SysDictKey::getStatus, Constants.SystemStatus.NORMAL);
            List<SysDictKey> dictKeys = sysDictKeyService.list(dictKeyLambdaQueryWrapper);

            if (dictKeys.isEmpty()) {
                log.info("没有找到启用的字典类型，跳过字典缓存加载");
                return false;
            }

            int successCount = 0;
            int failCount = 0;

            // 2. 为每个字典类型加载字典项到缓存
            for (SysDictKey dictKey : dictKeys) {
                try {
                    // 查询该字典类型下所有启用的字典项
                    LambdaQueryWrapper<SysDictValue> dictValueWrapper = new LambdaQueryWrapper<SysDictValue>()
                            .eq(SysDictValue::getDictKey, dictKey.getDictKey())
                            .eq(SysDictValue::getStatus, Constants.SystemStatus.NORMAL)
                            // 按排序字段升序
                            .orderByAsc(SysDictValue::getSort);

                    List<SysDictValue> dictValues = sysDictValueService.list(dictValueWrapper);

                    // 转换为Option格式
                    List<Option<String>> options = dictValues.stream()
                            .map(item -> new Option<>(item.getValue(), item.getLabel(), item.getTag()))
                            .toList();

                    // 3. 将数据放入缓存
                    String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictKey.getDictKey());
                    redisCache.setCacheObject(cacheKey, options, RedisConstants.DICT_CACHE_EXPIRE_TIME);

                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    log.error("字典类型 [{}] 缓存失败: {}", dictKey.getDictKey(), e.getMessage(), e);
                }
            }

            log.info("系统字典数据加载完成，成功: {} 个，失败: {} 个", successCount, failCount);

        } catch (Exception e) {
            log.error("加载系统字典数据失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean isAsync() {
        // 支持异步加载
        return true;
    }

    @Override
    public boolean allowStartupOnFailure() {
        // 字典加载失败不应阻止项目启动
        return true;
    }
}
