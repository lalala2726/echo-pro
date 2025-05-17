package cn.zhangchuangla.storage.config;

/**
 * 定义可配置回收站功能的接口。
 *
 * @author Chuang
 */
public interface TrashConfigurable {

    /**
     * 是否启用回收站功能。
     *
     * @return 如果启用则返回 true，否则返回 false。
     */
    boolean isEnableTrash();

    /**
     * 获取回收站目录的名称。
     *
     * @return 回收站目录名。
     */
    String getTrashDirectoryName();
}
