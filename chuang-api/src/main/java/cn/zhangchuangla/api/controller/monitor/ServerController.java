package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.infrastructure.model.entity.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * created on 2025/3/19 19:56
 */
@RestController
@RequestMapping("/monitor/server")
@Anonymous
@Tag(name = "服务器监控")
public class ServerController extends BaseController {
    /**
     * 服务器信息
     *
     * @return AjaxResult
     */
    @GetMapping
    @Operation(summary = "服务器信息")
    public AjaxResult getInfo() {
        Server server = new Server();
        server.copyInfo();
        return success(server);
    }
}
