package cn.zhangchuangla.system.loader;

import cn.zhangchuangla.common.core.core.loader.DataLoader;
import cn.zhangchuangla.common.core.model.entity.Option;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.service.SysDictItemService;
import cn.zhangchuangla.system.service.SysDictTypeService;
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

    private final SysDictTypeService sysDictTypeService;
    private final SysDictItemService sysDictItemService;
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
    public void load() {
        try {
            log.info("开始加载系统字典数据到缓存...");

            // 1. 查询所有启用的字典类型
            LambdaQueryWrapper<SysDictType> dictTypeWrapper = new LambdaQueryWrapper<SysDictType>()
                    // 0表示启用状态
                    .eq(SysDictType::getStatus, 0);
            List<SysDictType> dictTypes = sysDictTypeService.list(dictTypeWrapper);

            if (dictTypes.isEmpty()) {
                log.info("没有找到启用的字典类型，跳过字典缓存加载");
                return;
            }

            int successCount = 0;
            int failCount = 0;

            // 2. 为每个字典类型加载字典项到缓存
            for (SysDictType dictType : dictTypes) {
                try {
                    // 查询该字典类型下所有启用的字典项
                    LambdaQueryWrapper<SysDictItem> dictItemWrapper = new LambdaQueryWrapper<SysDictItem>()
                            .eq(SysDictItem::getDictType, dictType.getDictType())
                            // 0表示启用状态
                            .eq(SysDictItem::getStatus, 0)
                            // 按排序字段升序
                            .orderByAsc(SysDictItem::getSort);

                    List<SysDictItem> dictItems = sysDictItemService.list(dictItemWrapper);

                    // 转换为Option格式
                    List<Option<String>> options = dictItems.stream()
                            .map(item -> new Option<>(item.getItemValue(), item.getItemLabel(), item.getTag()))
                            .toList();

                    // 3. 将数据放入缓存
                    String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictType.getDictType());
                    redisCache.setCacheObject(cacheKey, options, RedisConstants.DICT_CACHE_EXPIRE_TIME);

                    successCount++;
                    log.debug("字典类型 [{}] 缓存成功，共 {} 个字典项", dictType.getDictType(), dictItems.size());

                } catch (Exception e) {
                    failCount++;
                    log.error("字典类型 [{}] 缓存失败: {}", dictType.getDictType(), e.getMessage(), e);
                }
            }

            log.info("系统字典数据加载完成，成功: {} 个，失败: {} 个", successCount, failCount);

        } catch (Exception e) {
            log.error("加载系统字典数据失败", e);
            throw new RuntimeException("系统字典加载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAsync() {
        // 支持异步加载
        return true;
    }
}
