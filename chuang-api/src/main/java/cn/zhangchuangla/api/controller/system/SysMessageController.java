package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.message.model.dto.MessageSendRequest;
import cn.zhangchuangla.message.service.SysMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统消息控制器
 *
 * @author Chuang
 * @since 2025/5/24 19:17
 */
@Slf4j
@RestController
@RequestMapping("/system/message")
@RequiredArgsConstructor
public class SysMessageController {

    private final SysMessageService sysMessageService;

}
