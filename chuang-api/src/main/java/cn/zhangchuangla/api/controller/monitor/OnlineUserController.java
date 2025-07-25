package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.redis.core.RedisKeyCache;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final RedisKeyCache<String> redisKeyCache;


}
