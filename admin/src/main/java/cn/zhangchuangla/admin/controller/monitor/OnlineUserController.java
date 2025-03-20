package cn.zhangchuangla.admin.controller.monitor;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.system.model.entity.OnlineUser;
import cn.zhangchuangla.system.model.request.monitor.OnlineUserListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/20 12:22
 */
@Slf4j
@RestController
@RequestMapping("/monitor/online")
@Tag(name = "在线用户管理")
public class OnlineUserController {

    private final RedisCache redisCache;

    public OnlineUserController(RedisCache redisCache) {
        this.redisCache = redisCache;
    }


    /**
     * 在线用户列表
     *
     * @param request 请求参数
     * @return 返回在线登录用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "在线用户列表")
    @PreAuthorize("@auth.hasPermission('monitor:online-user:list')")
    public AjaxResult onlineUserList(OnlineUserListRequest request) {
        Collection<String> keys = redisCache.keys(RedisKeyConstant.LOGIN_TOKEN_KEY + "*");
        ArrayList<OnlineUser> onlineUsers = new ArrayList<>();
        keys.forEach(key -> {
            LoginUser loginUser = redisCache.getCacheObject(key);
            OnlineUser onlineUser = new OnlineUser();
            BeanUtils.copyProperties(loginUser, onlineUser);
            onlineUsers.add(onlineUser);
        });
        Page<OnlineUser> page = PageUtils.getPage(request.getPageNum(), request.getPageSize(), onlineUsers.size(), onlineUsers);
        return AjaxResult.table(page);
    }


    /**
     * 强制退出登录
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "强制退出登录")
    @Log(title = "在线用户管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@auth.hasPermission('monitor:online-user:delete')")
    public AjaxResult forceLogout(@PathVariable("sessionId") String sessionId) {
        if (sessionId == null) {
            return AjaxResult.error(ResponseCode.PARAM_NOT_NULL);
        }
        redisCache.deleteObject(RedisKeyConstant.LOGIN_TOKEN_KEY + sessionId);
        return AjaxResult.success();
    }
}
