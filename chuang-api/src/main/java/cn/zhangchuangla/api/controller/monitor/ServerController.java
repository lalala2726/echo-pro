package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.framework.model.entity.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控接口
 * 提供服务器运行状态信息
 *
 * @author Chuang
 * created on 2025/3/19 19:56
 */
@RestController
@RequestMapping("/monitor/server")
@Tag(name = "服务器监控")
public class ServerController extends BaseController {

    public ServerController() {
        super();
    }

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    @GetMapping
    @Operation(summary = "服务器信息")
    @PreAuthorize("@ss.hasPermission('monitor:server:list')")
    public AjaxResult<Server> getInfo() {
        Server server = new Server();
        server.copyInfo();
        return success(server);
    }
}
