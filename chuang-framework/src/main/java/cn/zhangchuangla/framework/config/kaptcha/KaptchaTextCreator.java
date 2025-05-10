package cn.zhangchuangla.framework.config.kaptcha;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * 验证码文本生成器
 */
public class KaptchaTextCreator extends DefaultTextCreator {
    private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

    @Override
    public String getText() {
        int x = new Random().nextInt(10);
        int y = new Random().nextInt(10);
        StringBuilder mathResult = new StringBuilder();
        int randomOperator = new Random().nextInt(3);
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
