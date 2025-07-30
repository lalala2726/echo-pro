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
     * 校验是否为白名单配置
     *
     * @param invokeTarget 目标字符串
     * @return 结果
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
            String str = StringUtils.trimToEmpty(methodParam);
            // String字符串类型，包含'
            if (Strings.CS.contains(str, "'")) {
                classes.add(new Object[]{Strings.CS.replace(str, "'", ""), String.class});
            }
            // boolean布尔类型，等于true或者false
            else if (Strings.CS.contains(str, "true") || Strings.CS.contains(str, "false")) {
                classes.add(new Object[]{Boolean.valueOf(str), Boolean.class});
            }
            // long长整形，包含L
            else if (Strings.CS.contains(str, "L")) {
                classes.add(new Object[]{Long.valueOf(Strings.CS.replace(str, "L", "")), Long.class});
            }
            // double浮点类型，包含D
            else if (Strings.CS.contains(str, "D")) {
                classes.add(new Object[]{Double.valueOf(Strings.CS.replace(str, "D", "")), Double.class});
            }
            // 其他类型归类为整形
            else {
                classes.add(new Object[]{Integer.valueOf(str), Integer.class});
            }
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
     *
     * @param className 类名
     * @return 是否合法
     */
    private static boolean isValidClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return false;
        }

        // 检查是否包含危险字符
        for (String dangerStr : QuartzConstants.JOB_ERROR_STR) {
            if (Strings.CS.contains(className, dangerStr)) {
                return false;
            }
        }

        return true;
    }
}
