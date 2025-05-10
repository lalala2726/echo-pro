package cn.zhangchuangla.framework.aspect;

import cn.zhangchuangla.framework.annotation.RequiresSecondAuth;
import cn.zhangchuangla.system.service.SecondAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.lang.reflect.Method;


/**
 * @author Chuang
 * <p>
 * created on 2025/5/10 00:41
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SecondAuthAspect {


    private final SecondAuthService secondAuthService;

    /**
     * 处理 @RequiresSecondAuth 注解的方法
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(cn.zhangchuangla.framework.annotation.RequiresSecondAuth)")
    public Object handleSecondAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("AOP @RequiresSecondAuth: 拦截到方法 {}", joinPoint.getSignature().toShortString());

        // 1. 获取 HttpServletRequest 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.error("无法获取 ServletRequestAttributes，二次认证流程中止。");
            throw new SecurityException("无法处理请求，请稍后重试。");
        }
        String submittedPassword = getString(joinPoint, attributes);

        if (!StringUtils.hasText(submittedPassword)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            RequiresSecondAuth annotation = method.getAnnotation(RequiresSecondAuth.class);
            String passwordParamName = annotation != null ? annotation.passwordParam() : "password";
            log.warn("请求中未找到或为空的二次认证密码参数: '{}'", passwordParamName);
            throw new SecurityException("敏感操作需要二次认证，请在请求中提供参数 '" + passwordParamName + "'。");
        }

        // 5. 调用 SecondAuthService 进行密码验证
        boolean isAuthenticated;
        isAuthenticated = secondAuthService.verifyCurrentUserPassword(submittedPassword);
        if (isAuthenticated) {
            log.info("二次认证成功，用户: {}，方法: {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    joinPoint.getSignature().toShortString());
            // 验证成功，继续执行原方法
            return joinPoint.proceed();
        } else {
            log.warn("二次认证失败，密码不匹配，用户: {}，方法: {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    joinPoint.getSignature().toShortString());
            // 验证失败，返回禁止访问的响应
            throw new SecurityException("二次认证失败，密码错误或用户未认证。");
        }
    }

    private String getString(ProceedingJoinPoint joinPoint, ServletRequestAttributes attributes) throws IOException {
        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresSecondAuth annotation = method.getAnnotation(RequiresSecondAuth.class);

        String passwordParamName = "submittedPassword";
        if (annotation != null) {
            passwordParamName = annotation.passwordParam();
        }

        // 1. 尝试从请求参数中获取(表单数据或查询参数)
        String passwordValue = request.getParameter(passwordParamName);

        // 2. 如果未找到且请求是JSON格式,尝试从JSON正文中获取
        if (!StringUtils.hasText(passwordValue) &&
                request.getContentType() != null &&
                request.getContentType().toLowerCase().contains("application/json")) {

            log.debug("在请求参数中未找到参数 '{}'，尝试从JSON正文中读取。", passwordParamName);
            try {
                // 重要提示:读取输入流会消耗它。如果控制器方法也需要读取请求正文
                // (例如,使用@RequestBody用于其他目的),这种方法会导致问题,
                // 除非在上游使用ContentCachingRequestWrapper(例如,配置为servlet过滤器)。
                // 对于当前的SysLogController.cleanOperationLog,它不读取请求正文,
                // 所以在这个特定场景下直接读取应该是可以接受的。
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(request.getInputStream());
                if (rootNode.has(passwordParamName)) {
                    passwordValue = rootNode.get(passwordParamName).asText();
                    log.debug("成功从JSON正文中读取参数 '{}'。", passwordParamName);
                } else {
                    log.warn("在JSON正文中未找到参数 '{}'。", passwordParamName);
                }
            } catch (IOException e) {
                log.error("读取二次认证参数 '{}' 的JSON请求正文时出错: {}", passwordParamName, e.getMessage());
                // 这里不抛出异常,让现有逻辑处理缺失的passwordValue
            }
        }
        return passwordValue;
    }


}
