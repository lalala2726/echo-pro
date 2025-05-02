package cn.zhangchuangla.common.core.controller;

import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 19:09
 */
@Component
public class BaseController {


    /**
     * 将 List<T> 转换为 List<V>，使用 BeanUtils 进行属性拷贝
     *
     * @param sourceList  源数据列表
     * @param targetClass 目标类型的 Class
     * @param <T>         源数据类型
     * @param <V>         目标数据类型
     * @return 转换后的目标数据列表
     */
    protected static <T, V> List<V> copyListProperties(List<T> sourceList, Class<V> targetClass) {
        List<V> targetList = new ArrayList<>();
        try {
            for (T source : sourceList) {
                V target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                targetList.add(target);
            }
        } catch (Exception e) {
            throw new RuntimeException("List 属性拷贝失败", e);
        }
        return targetList;
    }


    /**
     * 将 Page<T> 转换为 List<V>，使用 BeanUtils 进行属性拷贝
     *
     * @param sourceList  源数据列表
     * @param targetClass 目标类型的 Class
     * @param <T>         源数据类型
     * @param <V>         目标数据类型
     * @return 转换后的目标数据列表
     */
    protected static <T, V> List<V> copyListProperties(Page<T> sourceList, Class<V> targetClass) {
        return copyListProperties(sourceList.getRecords(), targetClass);
    }

    /**
     * 封装分页数据,直接返回数据
     *
     * @param page 分页对象
     */
    protected TableDataResult getTableData(Page<?> page) {
        return TableDataResult.build(page);
    }

    /**
     * 封装分页数据,如果想要返回VO必须传入VO对象,否则返回的数据总数和页码不正确
     *
     * @param page 分页对象
     * @param rows 列表数据
     */
    protected TableDataResult getTableData(Page<?> page, List<?> rows) {
        return TableDataResult.build(page, rows);
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    protected SysUserDetails getLoginUser() {
        return SecurityUtils.getLoginUser();
    }


    /**
     * 获取当前用户名
     *
     * @return 当前用户名
     */
    protected String getUsername() {
        return SecurityUtils.getUsername();
    }

    /**
     * 获取当前用户id
     *
     * @return 当前用户id
     */
    protected Long getUserId() {
        return SecurityUtils.getUserId();
    }

    /**
     * 成功返回
     *
     * @return AjaxResult
     */
    protected <T> AjaxResult<T> success() {
        return AjaxResult.success();
    }

    /**
     * 失败返回
     *
     * @return AjaxResult
     */
    protected <T> AjaxResult<T> error() {
        return AjaxResult.error();
    }

    /**
     * 返回结果,根据boolean值返回成功或者失败
     */
    protected <T> AjaxResult<T> toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 返回结果,根据int值返回成功或者失败
     *
     * @param result int值
     * @return 结果
     */
    protected <T> AjaxResult<T> toAjax(int result) {
        return result > 0 ? success() : error();
    }

    /**
     * 返回结果,根据long值返回成功或者失败
     *
     * @param result long值
     * @return 结果
     */
    protected <T> AjaxResult<T> toAjax(long result) {
        return result > 0 ? success() : error();
    }

    /**
     * 成功返回
     */
    protected <T> AjaxResult<T> success(T data) {
        return AjaxResult.success(data);
    }

    /**
     * 成功返回
     *
     * @param message 消息
     * @return AjaxResult
     */
    protected <T> AjaxResult<T> success(String message) {
        return AjaxResult.success(message);
    }

    protected <T> AjaxResult<T> warning(String message) {
        return AjaxResult.warning(message);
    }

    /**
     * 失败返回
     *
     * @param message 消息
     */
    protected <T> AjaxResult<T> error(String message) {
        return AjaxResult.error(message);
    }


    /**
     * 失败返回
     *
     * @param responseCode 响应枚举
     * @return 结果
     */
    protected <T> AjaxResult<T> error(ResponseCode responseCode) {
        return AjaxResult.error(responseCode);
    }


    /**
     * 如果表达式为true则抛出异常
     *
     * @param conditionSupplier 条件
     * @param errorMessage      错误信息
     */
    protected void checkParam(boolean conditionSupplier, String errorMessage) {
        if (conditionSupplier) {
            throw new ParamException(errorMessage);
        }
    }

    /**
     * 加密密码
     *
     * @param password 密码
     * @return 加密后的密码
     */
    protected String encryptPassword(String password) {
        return SecurityUtils.encryptPassword(password);
    }

    /**
     * 验证密码是否匹配
     *
     * @param rawPassword     密码
     * @param encodedPassword 密码
     * @return 是否匹配
     */
    protected boolean matchesPassword(String rawPassword, String encodedPassword) {
        return SecurityUtils.matchesPassword(rawPassword, encodedPassword);
    }


    /**
     * 验证条件是否为true（函数方法）
     *
     * @param condition    条件
     * @param errorMessage 错误信息
     */
    protected void checkParam(Predicate<?> condition, String errorMessage) {
        if (condition.test(null)) {
            throw new ParamException(errorMessage);
        }
    }

}
