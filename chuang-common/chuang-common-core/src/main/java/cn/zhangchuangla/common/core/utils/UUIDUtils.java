package cn.zhangchuangla.common.core.utils;

import java.util.UUID;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/11 07:13
 */
public class UUIDUtils {

    /**
     * 获取一个简单UUID
     *
     * @return 简单UUID
     */
    public static String simple() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
