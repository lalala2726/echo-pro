package cn.zhangchuangla.common.utils;

import java.util.UUID;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/2 14:42
 */
public class UUIDUtils {

    /**
     * 生成UUID, 去掉了横线
     *
     * @return UUID
     */
    public static String simpleUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String UUID() {
        return UUID.randomUUID().toString();
    }


}
