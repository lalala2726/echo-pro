package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.app.model.entity.system.User;
import cn.zhangchuangla.app.model.request.system.UserRequest;
import cn.zhangchuangla.app.model.vo.system.UserVo;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:18
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户列表
     *
     * @param request 请求参数
     * @return 用户列表
     */
    public AjaxResult list(UserRequest request) {
        Page<User> userPage = userService.UserList(request);
        ArrayList<UserVo> listVos = new ArrayList<>();
        userPage.getRecords().forEach(user -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            listVos.add(userVo);
        });
        return AjaxResult.table(userPage, listVos);
    }

}
