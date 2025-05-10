package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.OnlineLoginUser;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.request.monitor.OnlineUserListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 在线用户管理接口
 * 提供在线用户列表和强制下线功能
 *
 * @author Chuang
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
     * @param request 在线用户列表查询参数
     * @return 返回在线登录用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "在线用户列表")
    @PreAuthorize("@ss.hasPermission('monitor:online-user:list')")
    public AjaxResult<TableDataResult> onlineUserList(@Parameter(description = "在线用户列表查询参数")
                                                      @Validated @ParameterObject OnlineUserListRequest request) {
        String replace = RedisConstants.Auth.ACCESS_TOKEN_USER.replace("{}", "*");
        Collection<String> keys = redisCache.keys(replace);
        ArrayList<OnlineLoginUser> onlineLoginUsers = new ArrayList<>();
        keys.forEach(key -> {
            OnlineLoginUser sysUserDetails = redisCache.getCacheObject(key);
            OnlineLoginUser onlineLoginUser = new OnlineLoginUser();
            BeanUtils.copyProperties(sysUserDetails, onlineLoginUser);
            onlineLoginUsers.add(onlineLoginUser);
        });
        Page<OnlineLoginUser> page = PageUtils.getPage(request.getPageNum(), request.getPageSize(), onlineLoginUsers.size(),
                onlineLoginUsers);
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
    public AjaxResult<Void> forceLogout(
            @PathVariable("sessionId") @Parameter(name = "会话ID", required = true) @NotBlank(message = "会话ID不能为空") String sessionId) {
        String replace = RedisConstants.Auth.ACCESS_TOKEN_USER.replace("{}", "");
        redisCache.deleteObject(replace + sessionId);
        return success();
    }
}
