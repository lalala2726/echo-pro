package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.framework.security.session.SessionService;
import cn.zhangchuangla.system.model.request.monitor.OnlineUserQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/28 14:41
 */
@RestController
@RequestMapping("/monitor/session")
@Slf4j
@RequiredArgsConstructor
public class SessionController extends BaseController {

    private final SessionService sessionService;

    @GetMapping("/list")
    public AjaxResult<TableDataResult> sessionList(OnlineUserQueryRequest request) {
        PageResult<OnlineLoginUser> onlineUserPageResult = sessionService.sessionList(request);
        return getTableData(onlineUserPageResult);

    }

}
