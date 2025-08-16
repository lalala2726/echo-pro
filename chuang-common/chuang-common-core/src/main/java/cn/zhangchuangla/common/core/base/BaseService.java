package cn.zhangchuangla.common.core.base;

import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 */
public interface BaseService {

    /**
     * 获取当前用户ID
     */
    default Long getUserId() {
        return SecurityUtils.getUserId();
    }

    /**
     * 获取当前用户名
     */
    default String getUsername() {
        return SecurityUtils.getUsername();
    }

    /**
     * 获取当前用户信息
     */
    default SysUserDetails getUser() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取当前用户角色
     */
    default Set<String> getRoles() {
        return SecurityUtils.getRoles();
    }

    /**
     * 对象属性拷贝
     */
    default <T, V> V copyProperties(T source, Class<V> targetClass) {
        return BeanCotyUtils.copyProperties(source, targetClass);
    }

    /**
     * 列表属性拷贝
     */
    default <T, V> List<V> copyListProperties(List<T> sourceList, Class<V> targetClass) {
        return BeanCotyUtils.copyListProperties(sourceList, targetClass);
    }

    /**
     * 分页对象属性拷贝
     */
    default <T, V> List<V> copyListProperties(Page<T> sourcePage, Class<V> targetClass) {
        return BeanCotyUtils.copyListProperties(sourcePage, targetClass);
    }

    /**
     * 加密密码
     */
    default String encryptPassword(String password) {
        return SecurityUtils.encryptPassword(password);
    }

    /**
     * 密码匹配
     *
     * @param rawPassword     原始密码(未加密啊)
     * @param encodedPassword 已加密的密码
     * @return 密码匹配结果
     */
    default boolean matchesPassword(String rawPassword, String encodedPassword) {
        return SecurityUtils.matchesPassword(rawPassword, encodedPassword);
    }
}
