package cn.zhangchuangla.storage.config;

/**
 * 回收站配置接口
 * <p>
 * 实现此接口的配置类支持回收站功能。
 * 存储提供者可以根据这些配置决定如何处理删除的文件。
 * </p>
 *
 * @author Chuang
 */
public interface TrashConfigurable {

    /**
     * 是否启用回收站功能
     *
     * @return true 启用回收站，false 禁用回收站（直接删除）
     */
    boolean isEnableTrash();

    /**
     * 获取回收站目录名
     *
     * @return 回收站目录名
     */
    String getTrashDirectoryName();
}
