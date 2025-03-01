package cn.zhangchuangla.common.enums;

import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:47
 */
@Getter
public enum ResponseCode {

    //region 通用成功与错误状态
    SUCCESS(200, "操作成功"),                          // 当操作成功完成时的响应
    ERROR(500, "操作失败"),                            // 当操作执行过程中发生内部错误时的响应
    SERVER_ERROR(500, "服务器错误"),                   // 当服务器内部发生错误时的响应
    // endregion

    //region 请求相关错误状态
    PARAM_ERROR(400, "参数错误!"),                     // 当请求参数有误或缺失时的响应
    PARAM_NOT_NULL(1009, "参数不能为空"),                // 当参数不能为空时的响应
    PARAM_ERROR_TOO_LARGE(40000, "请求参数过大"),       // 当请求参数过于庞大时的响应
    PARAM_ERROR_ZERO(40001, "参数不能小于0"),           // 当请求参数为0时的响应
    NOT_FOUND(404, "未找到该资源"),                    // 当请求的资源不存在时的响应
    NOT_SUPPORT(405, "不支持该请求"),                  // 当请求方法不被允许时的响应
    // endregion

    // region 认证与授权错误状态
    UNAUTHORIZED(401, "未授权"),                       // 当用户未通过身份验证时的响应
    FORBIDDEN(403, "禁止访问"),                        // 当用户没有访问资源的权限时的响应
    NOT_LOGIN(501, "未登录"),                          // 当用户尝试访问需要登录的资源但未登录时的响应
    USER_NOT_LOGIN(1004, "用户未登录"),
    TOKEN_EXPIRED(40015, "凭证已过期,请重新登录"),        // 凭证已过期
    TOKEN_EXPIRE(40016, "会话已过期,请重新登录"),         // 会话已过期
    TOKEN_MISS(40017, "缺少令牌"),                      // 缺少令牌
    AUTHORIZED(40011, "认证授权失败"),                 // 认证授权失败
    //endregion

    // region 用户相关错误状态
    USER_NOT_EXIST(1001, "用户不存在"),                 // 当指定的用户不存在时的响应
    USER_EXIST(1002, "用户已存在"),                     // 当尝试创建已存在的用户时的响应
    USER_PASSWORD_ERROR(1003, "用户名或密码错误"),       // 当用户提供的用户名或密码不正确时的响应
    USERNAME_FORMAT_ERROR(40012, "用户名不合法"),       // 用户名不合法
    PASSWORD_FORMAT_ERROR(40013, "密码不合法"),         // 密码不合法
    LOGIN_ERROR(40014, "登录失败"),                     // 登录失败
    USER_NOT_ADMIN(1005, "用户不是管理员"),              // 当需要管理员权限但用户不是管理员时的响应
    USER_NOT_ACTIVE(1006, "用户未激活"),                // 当用户账户未激活时的响应
    USER_NOT_VERIFY(1007, "用户未验证"),                // 当用户信息未验证时的响应
    USER_NOT_BIND(1008, "用户未绑定"),                  // 当用户未绑定必要信息时的响应
    // endregion

    // region 数据相关错误状态
    DATA_NOT_FOUND(40002, "数据未找到"),                // 当查询不到相关数据时的响应
    RESULT_IS_NULL(40006, "查询为空"),                  // 当查询结果为空时的响应
    DELETE_ERROR(40003, "删除失败"),                    // 当删除操作失败时的响应
    // endregion

    // region 字典相关错误状态
    DICT_NAME_EXIST(40004, "字典名称已存在"),            // 当字典名称重复时的响应
    DICT_TYPE_EXIST(40005, "字典类型已存在"),            // 当字典类型重复时的响应
    DICT_NAME_ERROR(40007, "只能是英文数字和下划线"),    // 当字典名称格式不正确时的响应
    // endregion

    // region 其他错误状态
    NOT_EXIST(502, "不存在"),                           // 当请求的资源不存在时的响应
    NOT_ALLOW(503, "不允许"),
    FileNameIsNull(40008, "文件名不能为空"),
    FileUploadFailed(40009, "文件上传失败"),             // 文件上传失败
    PROFILE_ERROR(40010, "配置文件错误"),
    ACCOUNT_LOCKED(10002, "账号被锁定"), ACCESS_DENIED(40001, "您没有权限访问本资源");                // 配置文件错误
    // endregion


    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;

    ResponseCode(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    /**
     * 根据状态码获取枚举值
     *
     * @param code 状态码
     * @return 对应的枚举值，如果没有匹配则返回 null
     */
    public static ResponseCode getByCode(Integer code) {
        for (ResponseCode rc : values()) {
            if (rc.getCode().equals(code)) {
                return rc;
            }
        }
        return null; // 如果找不到匹配的状态码，返回 null
    }

}
