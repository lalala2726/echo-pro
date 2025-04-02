package cn.zhangchuangla.storage.core;

/**
 * 文件存储操作基础接口
 * 定义所有存储实现必须实现的标准方法
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2
 */
public interface StorageOperation {
    /**
     * 保存文件
     *
     * @param fileName 文件名称
     * @param bytes    文件字节数组
     * @return 保存结果
     */
    boolean save(String fileName, byte[] bytes);

    /**
     * 读取文件
     *
     * @param fileName 文件名称
     * @return 文件字节数组
     */
    byte[] load(String fileName);

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     * @return 删除结果
     */
    boolean delete(String fileName);

    /**
     * 更新文件
     *
     * @param fileName 文件名称
     * @param bytes    文件字节数组
     * @return 操作结果
     */
    boolean update(String fileName, byte[] bytes);

    /**
     * 文件是否存在
     *
     * @param fileName 文件名称
     * @return true 存在 false 不存在
     */
    boolean exists(String fileName);
}
