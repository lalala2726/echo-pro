package cn.zhangchuangla.framework.web.service;


import cn.zhangchuangla.framework.model.request.RegisterRequest;

/**
 * 注册服务
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 15:02
 */
public interface RegisterService {

    /**
     * 注册
     *
     * @param request 请求参数
     * @return 返回结果
     */
    Long register(RegisterRequest request);

}
