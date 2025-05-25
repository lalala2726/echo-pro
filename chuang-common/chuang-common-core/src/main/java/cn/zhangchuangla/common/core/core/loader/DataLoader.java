package cn.zhangchuangla.common.core.core.loader;

/**
 * 数据加载器接口，所有模块加载器需实现此接口
 *
 * @author Chuang
 * <p>
 * created on 2025/5/22 16:24
 */
public interface DataLoader {
    /**
     * 获取加载器名称
     */
    String getName();

    /**
     * 获取加载优先级，数值越小优先级越高
     */
    int getOrder();

    /**
     * 执行数据加载
     */
    void load();

    /**
     * 是否允许异步加载
     */
    default boolean isAsync() {
        return false;
    }
}
