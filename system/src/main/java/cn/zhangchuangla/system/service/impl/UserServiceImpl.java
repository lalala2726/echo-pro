package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.app.model.entity.system.User;
import cn.zhangchuangla.app.model.request.system.UserRequest;
import cn.zhangchuangla.system.mapper.UserMapper;
import cn.zhangchuangla.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public Page<User> UserList(UserRequest request) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据用户名模糊查询
        userLambdaQueryWrapper.like(request.getUsername() != null && !request.getUsername().isEmpty(),
                User::getUsername, request.getUsername());
        //根据昵称模糊查询
        userLambdaQueryWrapper.like(request.getNickName() != null && !request.getNickName().isEmpty(),
                User::getNickName, request.getNickName());
        //根据邮箱模糊查询
        userLambdaQueryWrapper.like(request.getEmail() != null && !request.getEmail().isEmpty(),
                User::getEmail, request.getEmail());
        //根据状态精准查询
        userLambdaQueryWrapper.eq(request.getStatus() != null,
                User::getStatus, request.getStatus());
        //根据备注模糊查询
        userLambdaQueryWrapper.like(request.getRemark() != null && !request.getRemark().isEmpty(),
                User::getRemark, request.getRemark());
        //根据创建人精准查询
        userLambdaQueryWrapper.eq(request.getCreateBy() != null && !request.getCreateBy().isEmpty(),
                User::getCreateBy, request.getCreateBy());
        //根据修改人精准查询
        userLambdaQueryWrapper.eq(request.getUpdateBy() != null && !request.getUpdateBy().isEmpty(),
                User::getUpdateBy, request.getUpdateBy());

        return this.page(new Page<>(request.getPageNum(), request.getPageSize()), userLambdaQueryWrapper);
    }
}




