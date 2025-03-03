package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
    public static void minValidParam(Integer param, String message) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void minValidParam(Long param, String message) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

    /**
     * 参数不能为空和小于等于0
     *
     * @param param 参数
     */
    public static void minValidParam(List<Long> param, String message) {
        if (param == null || param.isEmpty() || param.stream().anyMatch(p -> p <= 0)) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void minValidParam(Integer param) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "参数不能为小于等于零");
        }
    }

    /**
     * 参数不能为空
     *
     * @param param 参数
     */
    public static void minValidParam(Long param) {
        if (param <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "参数不能为小于等于零");
        }
    }

    /**
     * /**
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

    /**
     * 对象不能为空
     *
     * @param object  对象
     * @param message 错误提示信息
     */
    public static void objectIsNull(Object object, String message) {
        if (object == null) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

    /**
     * 通常用于从数据库查询数据时，判断查询结果是否通过
     *
     * @param val     true为通过，false不通过
     * @param message 错误提示信息
     */
    public static void isParamValid(boolean val, String message) {
        if (!val) {
            throw new ParamException(ResponseCode.PARAM_ERROR, message);
        }
    }

}
