package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/24 17:33
 */
@Slf4j
public class ParamsUtils {


    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void paramNotIsZeroOrBlank(Integer param, String message) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void paramNotIsZeroOrBlank(Long param, String message) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void paramNotIsZeroOrBlank(Integer param) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "参数不能为小于等于零");
        }
    }

    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void paramNotIsZeroOrBlank(Long param) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "参数不能为小于等于零");
        }
    }

    /**
     * 参数不能为空
     *
     * @param params 参数
     */
    public static void paramsNotIsNullOrBlank(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new ParamException(ResponseCode.PARAM_ERROR, "参数不能为空");
            }
        }
    }

    /**
     * 参数不能为空
     *
     * @param message 提示信息
     * @param params  参数
     */
    public static void paramsNotIsNullOrBlank(String message, String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new ParamException(ResponseCode.PARAM_ERROR, message);
            }
        }
    }

}
