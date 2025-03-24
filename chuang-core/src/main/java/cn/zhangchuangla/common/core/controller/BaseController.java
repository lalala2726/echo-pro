package cn.zhangchuangla.common.core.controller;

import cn.zhangchuangla.common.constant.HttpStatusConstant;
import cn.zhangchuangla.common.constant.SystemMessageConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 19:09
 */
public class BaseController {


    /**
     * 封装分页数据,直接返回数据
     *
     * @param page 分页对象
     */
    protected TableDataResult getTableData(Page<?> page) {
        TableDataResult tableDataResult = new TableDataResult();
        tableDataResult.setCode(HttpStatusConstant.SUCCESS);
        tableDataResult.setMessage(SystemMessageConstant.QUERY_SUCCESS);
        tableDataResult.setTotal(page.getTotal());
        tableDataResult.setRows(page.getRecords());
        tableDataResult.setCurrentTime(System.currentTimeMillis());
        tableDataResult.setPageNum(page.getCurrent());
        tableDataResult.setPageSize(page.getSize());
        return tableDataResult;
    }

    /**
     * 封装分页数据,如果想要返回VO必须传入VO对象,否则返回的数据总数和页码不正确
     *
     * @param page 分页对象
     * @param vo   VO对象
     */
    protected TableDataResult getTableData(Page<?> page, List<?> vo) {
        TableDataResult tableDataResult = new TableDataResult();
        tableDataResult.setCode(HttpStatusConstant.SUCCESS);
        tableDataResult.setMessage(SystemMessageConstant.QUERY_SUCCESS);
        tableDataResult.setTotal(page.getTotal());
        tableDataResult.setRows(vo);
        tableDataResult.setCurrentTime(System.currentTimeMillis());
        tableDataResult.setPageNum(page.getCurrent());
        tableDataResult.setPageSize(page.getSize());
        return tableDataResult;
    }


    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    protected LoginUser getLoginUser() {
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
    protected AjaxResult success() {
        return AjaxResult.success();
    }

    /**
     * 失败返回
     *
     * @return AjaxResult
     */
    protected AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回结果,根据boolean值返回成功或者失败
     */
    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 返回结果,根据int值返回成功或者失败
     *
     * @param result int值
     * @return 结果
     */
    protected AjaxResult toAjax(int result) {
        return result > 0 ? success() : error();
    }

    /**
     * 返回结果,根据long值返回成功或者失败
     *
     * @param result long值
     * @return 结果
     */
    protected AjaxResult toAjax(long result) {
        return result > 0 ? success() : error();
    }

    /**
     * 成功返回
     */
    protected AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    /**
     * 成功返回
     *
     * @param message 消息
     * @return AjaxResult
     */
    protected AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    /**
     * 失败返回
     *
     * @param message 消息
     */
    protected AjaxResult error(String message) {
        return AjaxResult.error(message);
    }


    /**
     * 失败返回
     *
     * @param responseCode 响应枚举
     * @return 结果
     */
    protected AjaxResult error(ResponseCode responseCode) {
        return AjaxResult.error(responseCode);
    }

}
