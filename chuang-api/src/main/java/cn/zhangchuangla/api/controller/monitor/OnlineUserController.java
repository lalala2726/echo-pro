package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.OnlineLoginUser;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysOnlineUser;
import cn.zhangchuangla.system.model.request.monitor.OnlineUserListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OnlineUserController extends BaseController {

    private final RedisCache redisCache;


    /**
     * 在线用户列表
     *
     * @param request 请求参数
     * @return 返回在线登录用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "在线用户列表")
    @PreAuthorize("@ss.hasPermission('monitor:online-user:list')")
    public TableDataResult onlineUserList(OnlineUserListRequest request) {
        String replace = RedisConstants.Auth.ACCESS_TOKEN_USER.replace("{}", "*");
        Collection<String> keys = redisCache.keys(replace);
        ArrayList<SysOnlineUser> sysOnlineUsers = new ArrayList<>();
        keys.forEach(key -> {
            OnlineLoginUser sysUserDetails = redisCache.getCacheObject(key);
            SysOnlineUser sysOnlineUser = new SysOnlineUser();
            BeanUtils.copyProperties(sysUserDetails, sysOnlineUser);
            sysOnlineUsers.add(sysOnlineUser);
        });
        Page<SysOnlineUser> page = PageUtils.getPage(request.getPageNum(), request.getPageSize(), sysOnlineUsers.size(), sysOnlineUsers);
        return getTableData(page);
    }


    /**
     * 强制退出登录
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "强制退出登录")
    @OperationLog(title = "在线用户管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('monitor:online-user:delete')")
    public AjaxResult forceLogout(@PathVariable("sessionId") @Parameter(name = "会话ID", required = true) @NotBlank(message = "会话ID不能为空") String sessionId) {
        if (sessionId == null) {
            return error(ResponseCode.PARAM_NOT_NULL);
        }
        String replace = RedisConstants.Auth.ACCESS_TOKEN_USER.replace("{}", "");
        redisCache.deleteObject(replace + sessionId);
        return success();
    }
}
