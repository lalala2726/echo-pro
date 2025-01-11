package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.app.model.entity.system.User;
import cn.zhangchuangla.app.model.request.system.AddUserRequest;
import cn.zhangchuangla.app.model.request.system.UpdateUserRequest;
import cn.zhangchuangla.app.model.request.system.UserRequest;
import cn.zhangchuangla.app.model.vo.system.UserVo;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:18
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户列表
     *
     * @param request 请求参数
     * @return 用户列表
     */
    @GetMapping("/list")
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

    /**
     * 新增用户, 返回新增用户ID
     * @param request 新增用户请求参数
     * @return 新增成功返回用户主键,失败返回-1
     */
    @PostMapping
    public AjaxResult addUserInfo(@RequestBody AddUserRequest request){
        if (request == null){
            return AjaxResult.error("请求参数为空");
        }
        //检验手机号是否合法
        if (request.getPhone() != null && !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            return AjaxResult.error("手机号不合法");
        }
        //用户名必须大于5位小于16位
        if (request.getUsername() == null || request.getUsername().length() < 5 || request.getUsername().length() > 16) {
            return AjaxResult.error("用户名不合法");
        }
        //检验邮箱是否合法
        if (request.getEmail() != null && !request.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            return AjaxResult.error("邮箱不合法");
        }
        //密码必须大于6位小于16位
        if (request.getPassword() == null || request.getPassword().length() < 6 || request.getPassword().length() > 16) {
            return AjaxResult.error("密码不合法");
        }
        if (userService.isUsernameExist(request.getUsername())) {
            return AjaxResult.error("用户名已存在");
        }
        if (request.getEmail() != null && userService.isEmailExist(request.getEmail())) {
            return AjaxResult.error("邮箱已存在");
        }
        if (request.getPassword() != null &&userService.isPhoneExist(request.getPhone())) {
            return AjaxResult.error("手机号已存在");
        }
        return AjaxResult.toSuccess(userService.addUserInfo(request));
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除成功返回true,失败返回false
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteUser(@PathVariable("id") Long id){
        if (id == null){
            return AjaxResult.error("用户ID为空");
        }
        return AjaxResult.isSuccess(userService.removeById(id));
    }


    /**
     * 修改用户信息
     * @param request 修改用户信息
     * @return 修改成功返回true,失败返回false
     */
    @PutMapping
    public AjaxResult updateUserInfoById(@RequestBody UpdateUserRequest request){
        if (request.getId() == null || request.getUsername() == null){
            return AjaxResult.error(ResponseCode.PARAM_ERROR, "用户ID不能为空或不能小于0!");
        }
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return AjaxResult.isSuccess(userService.updateById(user));
    }

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public AjaxResult getUserInfoById(@PathVariable("id") Long id){
        if (id == null){
            return AjaxResult.error("用户ID为空");
        }
        User user = userService.getById(id);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return AjaxResult.success(userVo);
    }

}
