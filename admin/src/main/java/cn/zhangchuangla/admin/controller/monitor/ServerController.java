package cn.zhangchuangla.admin.controller.monitor;

import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.framework.model.entity.Server;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ServerController {
    /**
     * 服务器信息
     *
     * @return AjaxResult
     */
    @GetMapping
    @Schema(description = "服务器信息")
    public AjaxResult getInfo() {
        Server server = new Server();
        server.copyInfo();
        return AjaxResult.success(server);
    }
}
