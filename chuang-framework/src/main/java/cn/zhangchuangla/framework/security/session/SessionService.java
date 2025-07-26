package cn.zhangchuangla.framework.security.session;

import cn.zhangchuangla.framework.security.model.entity.SessionDevice;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录设备管理（查询、删除、踢下线）
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 22:06
 */
@Service
public class SessionService {


    /**
     * 获取全部登录设备
     *
     * @return 返回系统中全部登录设备
     */
    public List<SessionDevice> listDevice() {
        return null;
    }

}
