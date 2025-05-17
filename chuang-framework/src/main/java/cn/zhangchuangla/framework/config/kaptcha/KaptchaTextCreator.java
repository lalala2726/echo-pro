package cn.zhangchuangla.framework.config.kaptcha;

import java.security.SecureRandom;

/**
 * 安全的验证码文本生成器
 *
 * @author zhangchuang
 */
public class KaptchaTextCreator {
    private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 生成随机数学算式文本
     *
     * @return 数学算式文本，格式为"算式=?@答案"
     */
    public String getText() {
        int x = secureRandom.nextInt(10);
        int y = secureRandom.nextInt(10);
        StringBuilder mathResult = new StringBuilder();
        int randomOperator = secureRandom.nextInt(3);
        switch (randomOperator) {
            case 0:
                mathResult.append(x);
                mathResult.append("+");
                mathResult.append(y);
                mathResult.append("=?@");
                mathResult.append(x + y);
                break;
            case 1:
                if (x < y) {
                    int temp = x;
                    x = y;
                    y = temp;
                }
                mathResult.append(x);
                mathResult.append("-");
                mathResult.append(y);
                mathResult.append("=?@");
                mathResult.append(x - y);
                break;
            case 2:
                mathResult.append(x);
                mathResult.append("×");
                mathResult.append(y);
                mathResult.append("=?@");
                mathResult.append(x * y);
                break;
        }
        return mathResult.toString();
    }
}
