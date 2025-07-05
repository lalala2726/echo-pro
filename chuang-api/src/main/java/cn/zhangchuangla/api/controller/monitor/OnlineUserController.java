package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.core.result.AjaxResult;
import cn.zhangchuangla.common.core.core.result.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.PageUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.request.monitor.OnlineUserQueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
@Tag(name = "在线用户管理", description = "提供在线用户列表和强制下线功能")
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
                                                      @Validated @ParameterObject OnlineUserQueryRequest request) {
        String pattern = RedisConstants.Auth.ACCESS_TOKEN_USER + "*";
        Collection<String> keys = redisCache.keys(pattern);
        List<OnlineLoginUser> matchedUsers = new ArrayList<>();

        // 过滤匹配的在线用户
        for (String key : keys) {
            OnlineLoginUser user = redisCache.getCacheObject(key);
            if (user != null && matchesFilter(user, request)) {
                matchedUsers.add(user);
            }
        }
        // 手动分页处理
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int total = matchedUsers.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<OnlineLoginUser> paginatedList = matchedUsers.subList(start, end);

        return TableDataResult.build(PageUtils.getPage(pageNum, pageSize, total, paginatedList));
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
        redisCache.deleteObject(RedisConstants.Auth.ACCESS_TOKEN_USER + sessionId);
        return success();
    }

    /**
     * 根据查询条件过滤在线用户
     *
     * @param user    在线用户信息
     * @param request 查询请求参数
     * @return 是否匹配过滤条件
     */
    private boolean matchesFilter(OnlineLoginUser user, OnlineUserQueryRequest request) {
        // 会话ID匹配
        if (StringUtils.isNotBlank(request.getSessionId()) &&
                !request.getSessionId().equals(user.getSessionId())) {
            return false;
        }

        // 用户名匹配
        if (StringUtils.isNotBlank(request.getUsername()) &&
                !request.getUsername().contains(user.getUsername())) {
            return false;
        }

        // 用户ID匹配
        if (request.getUserId() != null &&
                !request.getUserId().equals(user.getUserId())) {
            return false;
        }

        // IP地址匹配
        if (StringUtils.isNotBlank(request.getIp()) &&
                !request.getIp().equals(user.getIP())) {
            return false;
        }

        // 登录地点匹配
        if (StringUtils.isNotBlank(request.getRegion()) &&
                !request.getRegion().contains(user.getRegion())) {
            return false;
        }

        // 浏览器匹配
        if (StringUtils.isNotBlank(request.getBrowser()) &&
                !request.getBrowser().equals(user.getBrowser())) {
            return false;
        }

        // 操作系统匹配
        return !StringUtils.isNotBlank(request.getOs()) ||
                request.getOs().equals(user.getOs());
    }
}
