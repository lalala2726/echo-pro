package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import cn.zhangchuangla.framework.security.session.SessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Chuang
 * created on 2025/3/20 12:22
 */
@Slf4j
@RestController
@RequestMapping("/monitor/session")
@Tag(name = "会话管理", description = "会话相关管理")
@RequiredArgsConstructor
public class SessionController extends BaseController {

    private final SessionService sessionService;


    @GetMapping("/list")
    public AjaxResult<List<SessionDevice>> listDevice(SessionDeviceQueryRequest request) {
        List<SessionDevice> sessionDevices = sessionService.listDevice(request);
        return success(sessionDevices);
    }


}
