package cn.zhangchuangla.common.constant;

/**
 * 正则表达式常量
 *
 * @author Chuang
 * <p>
 * created on 2025/4/20 09:05
 */

public interface RegularConstants {

    /**
     * 用户相关正则表达式
     */
    interface User {
        String password = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9!@#¥%&*（）——+]{8,20}$";
        String username = "^[a-zA-Z0-9_]{5,20}$";
        String phone = "^1[3-9]\\d{9}$";
        String email = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
    }

    /**
     * 存储相关
     */
    interface Storage {
        String domain = "^(https?://)?((([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(:(\\d+))?(/[^/]*)?$";
        String bucketName = "[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$";
    }
}
