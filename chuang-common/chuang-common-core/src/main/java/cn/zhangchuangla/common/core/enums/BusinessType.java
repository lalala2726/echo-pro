package cn.zhangchuangla.common.core.enums;

/**
 * 业务操作类型
 *
 * @author Chuang
 */
public enum BusinessType {
    /**
     * 其它
     */
    OTHER,

    /**
     * 新增
     */
    INSERT,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 授权
     */
    GRANT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 清空数据
     */
    CLEAN,

    /**
     * 登录
     */
    LOGIN,

    /**
     * 退出
     */
    LOGOUT,

    /**
     * 注册
     */
    REGISTER,

    /**
     * 密码重置
     */
    RESET_PWD,

    /**
     * 刷新
     */
    REFRESH,

    /**
     * 发送消息
     */
    SEND_MESSAGES,

    /**
     * 上传
     */
    UPLOAD,

    /**
     * 恢复
     */
    RECOVER,

    /**
     * 生成代码
     */
    GENERATE,

    /**
     * 执行SQL
     */
    EXECUTE,
}
