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

    /**
     * 发送消息
     *
     * @param request 消息发送请求
     * @return 响应结果
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@Validated @RequestBody MessageSendRequest request) {
        try {
            log.info("接收到消息发送请求，标题: {}, 目标类型: {}", request.getTitle(), request.getTargetType());

            Long messageId = sysMessageService.sendMessage(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "消息发送成功");
            result.put("data", Map.of("messageId", messageId));

            log.info("消息发送请求处理成功，消息ID: {}", messageId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("消息发送失败: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "消息发送失败: " + e.getMessage());
            result.put("data", null);

            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 立即发送消息（用于重试或手动触发）
     *
     * @param messageId 消息ID
     * @return 响应结果
     */
    @PostMapping("/send/{messageId}")
    public ResponseEntity<Map<String, Object>> sendMessageImmediately(@PathVariable Long messageId) {
        try {
            log.info("接收到立即发送消息请求，消息ID: {}", messageId);

            sysMessageService.sendMessageImmediately(messageId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "消息发送成功");
            result.put("data", null);

            log.info("立即发送消息成功，消息ID: {}", messageId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("立即发送消息失败，消息ID: {}, 错误: {}", messageId, e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "消息发送失败: " + e.getMessage());
            result.put("data", null);

            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 测试发送消息给指定用户
     *
     * @return 响应结果
     */
    @PostMapping("/test/send")
    public ResponseEntity<Map<String, Object>> testSendMessage() {
        try {
            MessageSendRequest request = new MessageSendRequest();
            request.setTitle("测试消息");
            request.setContent("这是一条测试消息，用于验证站内信功能是否正常工作。");
            // 系统消息
            request.setType(1);
            // 普通级别
            request.setLevel(1);
            request.setSenderName("系统");
            // 指定用户
            request.setTargetType(1);
            // 测试用户ID
            request.setTargetUserIds(java.util.Arrays.asList(1L, 2L, 3L));
            // 仅站内信
            request.setPushType(1);

            Long messageId = sysMessageService.sendMessage(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "测试消息发送成功");
            result.put("data", Map.of("messageId", messageId));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("测试消息发送失败: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "测试消息发送失败: " + e.getMessage());
            result.put("data", null);

            return ResponseEntity.badRequest().body(result);
        }
    }
}
