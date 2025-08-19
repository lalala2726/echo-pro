package cn.zhangchuangla.quartz.util;

import cn.zhangchuangla.common.core.utils.SpringUtils;
import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.entity.SysJob;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务执行工具
 *
 * @author Chuang
 */
public class JobInvokeUtil {

    static {
        LoggerFactory.getLogger(JobInvokeUtil.class);
    }

    /**
     * 执行方法
     *
     * @param sysJob 系统任务
     */
    public static void invokeMethod(SysJob sysJob) throws Exception {
        String invokeTarget = sysJob.getInvokeTarget();
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);
        List<Object[]> methodParams = getMethodParams(invokeTarget);

        if (!isValidClassName(beanName)) {
            throw new SecurityException("非法的类名: " + beanName);
        }

        Object bean = SpringUtils.getBean(beanName);
        invokeMethod(bean, methodName, methodParams);
    }

    /**
     * 调用任务方法
     *
     * @param bean         目标对象
     * @param methodName   方法名称
     * @param methodParams 方法参数
     */
    private static void invokeMethod(Object bean, String methodName, List<Object[]> methodParams)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        if (methodParams != null && !methodParams.isEmpty()) {
            Method method = bean.getClass().getDeclaredMethod(methodName, getMethodParamsType(methodParams));
            method.invoke(bean, getMethodParamsValue(methodParams));
        } else {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.invoke(bean);
        }
    }

    /**
     * 是否不在白名单中（为保持向后兼容保留旧方法名的行为）
     * 返回 true 表示不在白名单（禁止），false 表示在白名单（允许）
     */
    public static boolean whiteList(String invokeTarget) {
        String packageName = getPackageName(invokeTarget);
        int count = 0;
        for (String str : QuartzConstants.JOB_WHITELIST_STR) {
            if (packageName.startsWith(str)) {
                count++;
            }
        }
        return count <= 0;
    }

    /**
     * 获取包名
     *
     * @param invokeTarget 目标字符串
     * @return 包名
     */
    public static String getPackageName(String invokeTarget) {
        String beanName = getBeanName(invokeTarget);
        if (StringUtils.isNotEmpty(beanName)) {
            Object bean = SpringUtils.getBean(beanName);
            return bean.getClass().getPackage().getName();
        }
        return "";
    }

    /**
     * 获取bean名称
     *
     * @param invokeTarget 目标字符串
     * @return bean名称
     */
    public static String getBeanName(String invokeTarget) {
        String beanName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringBeforeLast(beanName, ".");
    }

    /**
     * 获取bean方法
     *
     * @param invokeTarget 目标字符串
     * @return method方法
     */
    public static String getMethodName(String invokeTarget) {
        String methodName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringAfterLast(methodName, ".");
    }

    /**
     * 获取method方法参数相关列表
     *
     * @param invokeTarget 目标字符串
     * @return method方法相关参数列表
     */
    public static List<Object[]> getMethodParams(String invokeTarget) {
        String methodStr = StringUtils.substringBetween(invokeTarget, "(", ")");
        if (StringUtils.isEmpty(methodStr)) {
            return null;
        }
        String[] methodParams = methodStr.split(",(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
        List<Object[]> classes = new LinkedList<>();
        for (String methodParam : methodParams) {
            String raw = StringUtils.trimToEmpty(methodParam);
            // String: 单引号包围
            if (raw.startsWith("'") && raw.endsWith("'")) {
                classes.add(new Object[]{raw.substring(1, raw.length() - 1), String.class});
                continue;
            }
            // boolean: 严格匹配 true/false（不区分大小写）
            if ("true".equalsIgnoreCase(raw) || "false".equalsIgnoreCase(raw)) {
                classes.add(new Object[]{Boolean.valueOf(raw), Boolean.class});
                continue;
            }
            // long: 以 L 结尾
            if (raw.endsWith("L") || raw.endsWith("l")) {
                String num = raw.substring(0, raw.length() - 1);
                classes.add(new Object[]{Long.valueOf(num), Long.class});
                continue;
            }
            // double: 以 D 结尾
            if (raw.endsWith("D") || raw.endsWith("d")) {
                String num = raw.substring(0, raw.length() - 1);
                classes.add(new Object[]{Double.valueOf(num), Double.class});
                continue;
            }
            // 小数: 包含小数点
            if (raw.contains(".")) {
                classes.add(new Object[]{Double.valueOf(raw), Double.class});
                continue;
            }
            // 整数
            classes.add(new Object[]{Integer.valueOf(raw), Integer.class});
        }
        return classes;
    }

    /**
     * 获取参数类型
     *
     * @param methodParams 参数相关列表
     * @return 参数类型列表
     */
    public static Class<?>[] getMethodParamsType(List<Object[]> methodParams) {
        Class<?>[] classes = new Class<?>[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            classes[index] = (Class<?>) os[1];
            index++;
        }
        return classes;
    }

    /**
     * 获取参数值
     *
     * @param methodParams 参数相关列表
     * @return 参数值列表
     */
    public static Object[] getMethodParamsValue(List<Object[]> methodParams) {
        Object[] classes = new Object[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            classes[index] = os[0];
            index++;
        }
        return classes;
    }

    /**
     * 校验类名是否合法
     * 增强安全检查：包名白名单、危险字符检查、类名格式验证
     *
     * @param className 类名
     * @return 是否合法
     */
    private static boolean isValidClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return false;
        }

        // 检查类名长度（防止过长的恶意类名）
        if (className.length() > 200) {
            return false;
        }

        // 检查是否包含危险字符
        for (String dangerStr : QuartzConstants.JOB_ERROR_STR) {
            if (Strings.CS.contains(className, dangerStr)) {
                return false;
            }
        }

        // 验证Java类名格式
        if (!isValidJavaClassName(className)) {
            return false;
        }

        // 检查是否在白名单包中
        if (!isInWhitelist(className)) {
            return false;
        }

        return true;
    }

    /**
     * 验证是否为有效的Java类名格式
     *
     * @param className 类名
     * @return 是否有效
     */
    private static boolean isValidJavaClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return false;
        }

        // 检查是否包含非法字符（只允许字母、数字、点、下划线）
        if (!className.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$")) {
            return false;
        }

        // 检查是否以数字开头
        if (Character.isDigit(className.charAt(0))) {
            return false;
        }

        // 检查连续的点
        if (className.contains("..")) {
            return false;
        }

        // 检查是否以点开头或结尾
        if (className.startsWith(".") || className.endsWith(".")) {
            return false;
        }

        return true;
    }

    /**
     * 检查类名是否在白名单包中
     *
     * @param className 类名
     * @return 是否在白名单中
     */
    private static boolean isInWhitelist(String className) {
        String packageName = getPackageNameFromClassName(className);
        for (String whitelistPackage : QuartzConstants.JOB_WHITELIST_STR) {
            if (packageName.startsWith(whitelistPackage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从类名中提取包名
     *
     * @param className 完整类名
     * @return 包名
     */
    private static String getPackageNameFromClassName(String className) {
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return className.substring(0, lastDotIndex);
        }
        return "";
    }
}
