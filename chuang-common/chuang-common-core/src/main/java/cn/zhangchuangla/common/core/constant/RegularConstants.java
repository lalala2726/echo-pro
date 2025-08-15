package cn.zhangchuangla.common.core.constant;

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
        String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9!@#¥%&*（）——+]{8,20}$";
        String USERNAME = "^[a-zA-Z0-9_]{4,20}$";
        String PHONE = "^1[3-9]\\d{9}$";
        String EMAIL = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
    }
}
