package cn.zhangchuangla.system.service;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/10 00:42
 */
public interface SecondAuthService {

    //todo 移动到framework模块中

    /**
     * 验证当前用户的密码
     *
     * @param submittedPassword 提交的密码
     * @return 如果密码正确，返回true；否则返回false
     */
    boolean verifyCurrentUserPassword(String submittedPassword);


}
