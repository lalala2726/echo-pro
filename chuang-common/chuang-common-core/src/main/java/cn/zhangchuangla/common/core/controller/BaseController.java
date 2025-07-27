package cn.zhangchuangla.common.core.controller;

import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.PageResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
        return BeanCotyUtils.copyListProperties(sourceList, targetClass);
    }

    /**
     * 将 source 对象的属性复制到一个新的 targetClass 实例中
     *
     * @param source      原对象
     * @param targetClass 目标类 class 对象
     * @return 拷贝后的目标对象
     */
    protected static <T, V> V copyProperties(T source, Class<V> targetClass) {
        return BeanCotyUtils.copyProperties(source, targetClass);
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
        return BeanCotyUtils.copyListProperties(sourceList, targetClass);
    }

    /**
     * 封装分页数据,直接返回数据
     *
     * @param page 分页对象
     */
    protected AjaxResult<TableDataResult> getTableData(Page<?> page) {
        return TableDataResult.build(page);
    }

    /**
     * 封装分页数据,如果想要返回VO必须传入VO对象,否则返回的数据总数和页码不正确
     *
     * @param page 分页对象
     * @param rows 列表数据
     */
    protected AjaxResult<TableDataResult> getTableData(Page<?> page, List<?> rows) {
        return TableDataResult.build(page, rows);
    }

    /**
     * 封装分页数据,如果想要返回VO必须传入VO对象,否则返回的数据总数和页码不正确
     *
     * @param page  分页对象
     * @param rows  列表数据
     * @param extra 额外的数据
     * @return 封装后的分页数据
     */
    protected AjaxResult<TableDataResult> getTableData(Page<?> page, List<?> rows, Map<String, Object> extra) {
        return TableDataResult.build(page, rows, extra);
    }

    /**
     * 使用自定义分页封装分页结果
     *
     * @param page 自定义分页
     * @return 封装后的分页数据
     */
    protected AjaxResult<TableDataResult> getTableData(PageResult<?> page) {
        return TableDataResult.build(page);
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

    /**
     * 警告返回
     *
     * @param message 消息
     * @return AjaxResult
     */
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
     * @param resultCode 响应枚举
     * @return 结果
     */
    protected <T> AjaxResult<T> error(ResultCode resultCode) {
        return AjaxResult.error(resultCode);
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


}
