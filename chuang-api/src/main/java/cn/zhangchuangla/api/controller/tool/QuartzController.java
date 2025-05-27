package cn.zhangchuangla.api.controller.tool;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务调度控制器
 *
 * @author Chuang
 * @since 2025-01-27
 */
@Slf4j
@RestController
@RequestMapping("/tool/quartz")
@RequiredArgsConstructor
@Tag(name = "定时任务管理", description = "定时任务调度相关接口")
public class QuartzController {
}
