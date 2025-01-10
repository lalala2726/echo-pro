package cn.zhangchuangla.system.service;

import cn.zhangchuangla.app.model.entity.system.User;
import cn.zhangchuangla.app.model.request.system.UserRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    Page<User> UserList(UserRequest request);
}
