package cn.zhangchuangla.common.result;

import cn.zhangchuangla.common.enums.ResponseCode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/5 13:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AjaxResult extends HashMap<String, Object> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private static final String CODE_TAG = "code";

    /**
     * 返回消息
     */
    private static final String MSG_TAG = "message";

    /**
     * 时间
     */
    private static final String TIME_TAG = "time";

    /**
     * 返回数据
     */
    private static final String DATA_TAG = "data";

    /**
     * 时间戳
     */
    private static final String CURRENT_TIME = "currentTime";

    /**
     * 默认构造函数，成功返回
     */
    public AjaxResult() {
        this.put(CODE_TAG, ResponseCode.SUCCESS.getCode());
        this.put(MSG_TAG, ResponseCode.SUCCESS.getMessage());
        this.put(TIME_TAG, getCurrentTime());
        this.put(CURRENT_TIME, System.currentTimeMillis());
    }

    /**
     * 构造函数
     *
     * @param code 响应码枚举
     */
    public AjaxResult(ResponseCode code) {
        this.put(CODE_TAG, code.getCode());
        this.put(TIME_TAG, getCurrentTime());
        this.put(MSG_TAG, code.getMessage());
        this.put(CURRENT_TIME, System.currentTimeMillis());

    }

    /**
     * 构造函数
     *
     * @param code 响应码枚举
     * @param data 返回的数据
     */
    public AjaxResult(ResponseCode code, Object data) {
        this.put(CODE_TAG, code.getCode());
        this.put(MSG_TAG, code.getMessage());
        this.put(TIME_TAG, getCurrentTime());
        this.put(DATA_TAG, data);
        this.put(CURRENT_TIME, System.currentTimeMillis());
    }

    /**
     * 成功返回（带消息）
     *
     * @param msg 返回消息
     * @return AjaxResult
     */
    public static AjaxResult success(String msg) {
        AjaxResult result = new AjaxResult(ResponseCode.SUCCESS);
        result.put(MSG_TAG, msg);
        return result;
    }

    /**
     * 成功返回（不带消息）
     *
     * @return AjaxResult
     */
    public static AjaxResult success() {
        AjaxResult result = new AjaxResult(ResponseCode.SUCCESS);
        result.put(MSG_TAG, ResponseCode.SUCCESS.getMessage());
        return result;
    }


    /**
     * 成功返回（带数据）
     *
     * @param data 返回的数据
     * @return AjaxResult
     */
    public static AjaxResult success(Object data) {
        return new AjaxResult(ResponseCode.SUCCESS, data);
    }

    /**
     * 表格返回
     *
     * @param page 分页对象
     * @param vo   VO对象数据
     * @return 返回分页的列表信息
     */
    public static <T> AjaxResult table(Page<T> page, Object vo) {
        return getAjaxResult(vo, page.getTotal(), page.getSize(), page.getCurrent(), page);
    }

    /**
     * 表格返回
     *
     * @param data    数据
     * @param total   总数
     * @param size    每页显示的条数
     * @param current 当前页码
     * @param page    page信息
     * @param <T>     返回的对象信息
     * @return AjaxResult
     */
    private static <T> AjaxResult getAjaxResult(Object data, long total, long size, long current, Page<T> page) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.put("code", ResponseCode.SUCCESS.getCode());
        ajaxResult.put("message", ResponseCode.SUCCESS.getMessage());
        ajaxResult.put("data", data);
        ajaxResult.put("pageNum", current);
        ajaxResult.put("pageSize", size);
        ajaxResult.put("total", total);
        ajaxResult.put("time", getCurrentTime());
        return ajaxResult;
    }

    /**
     * 表格返回
     *
     * @param page 分页对象
     * @return 返回分页的列表信息
     */
    public static <T> AjaxResult table(Page<T> page) {
        if (page == null) {
            return AjaxResult.error(ResponseCode.ERROR, "分页对象为空");
        }
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.put("code", ResponseCode.SUCCESS.getCode());
        ajaxResult.put("message", ResponseCode.SUCCESS.getMessage());
        ajaxResult.put("data", page.getRecords());
        ajaxResult.put("pageNum", page.getCurrent());
        ajaxResult.put("pageSize", page.getSize());
        ajaxResult.put("total", page.getTotal());
        ajaxResult.put("time", getCurrentTime());
        ajaxResult.put("currentTime", System.currentTimeMillis());
        return ajaxResult;
    }

    /**
     * 判断是否成功
     *
     * @param value 参数
     * @return AjaxResult
     */
    public static AjaxResult isSuccess(boolean value) {
        if (!value) return AjaxResult.error(ResponseCode.ERROR);
        return new AjaxResult(ResponseCode.SUCCESS);
    }

    /**
     * 判断是否成功
     *
     * @param val 参数
     * @return AjaxResult
     */
    public static AjaxResult toSuccess(Long val) {
        if (val < 0) return AjaxResult.error(ResponseCode.ERROR);
        return new AjaxResult(ResponseCode.SUCCESS);
    }

    /**
     * 判断是否成功
     *
     * @param val 参数
     * @return AjaxResult
     */
    public static AjaxResult toSuccess(Integer val) {
        if (val < 0) return AjaxResult.error(ResponseCode.ERROR);
        return new AjaxResult(ResponseCode.SUCCESS);
    }

    /**
     * 失败返回（带消息）
     *
     * @param msg 返回消息
     * @return AjaxResult
     */
    public static AjaxResult error(String msg) {
        AjaxResult result = new AjaxResult(ResponseCode.ERROR);
        result.put(MSG_TAG, msg);
        return result;
    }

    /**
     * 失败返回（使用指定的响应码）
     *
     * @param code 响应码枚举
     * @return AjaxResult
     */
    public static AjaxResult error(ResponseCode code) {
        return new AjaxResult(code);
    }

    /**
     * 失败返回（带消息和响应码）
     *
     * @param msg  返回消息
     * @param code 错误代码
     * @return AjaxResult
     */
    public static AjaxResult error(String msg, Integer code) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.put(MSG_TAG, msg);
        ajaxResult.put(CODE_TAG, code);
        ajaxResult.put(TIME_TAG, getCurrentTime());
        return ajaxResult;
    }

    /**
     * 错误返回（带消息和响应码）
     *
     * @param responseCode 状态码
     * @param message      错误信息
     */
    public static AjaxResult error(ResponseCode responseCode, String message) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.put(MSG_TAG, message);
        ajaxResult.put(CODE_TAG, responseCode.getCode());
        ajaxResult.put(TIME_TAG, getCurrentTime());
        return ajaxResult;
    }

    /**
     * 获取当前时间，格式为 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的字符串
     */
    private static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
