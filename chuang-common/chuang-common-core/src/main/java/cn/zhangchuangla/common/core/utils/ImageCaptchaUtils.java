package cn.zhangchuangla.common.core.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * 图形验证码工具类
 * 生成包含干扰元素的 PNG Base64 图片
 */
public final class ImageCaptchaUtils {

    private static final String DATA_URI_PREFIX = "data:image/png;base64,";

    private ImageCaptchaUtils() {
    }

    public static String generateBase64Png(String code, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        // 背景
        g2.setColor(randomColor(230, 255));
        g2.fillRect(0, 0, width, height);

        // 抗锯齿与渲染质量
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 画干扰线
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            g2.setColor(randomColor(150, 220));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2.drawLine(x1, y1, x2, y2);
        }

        // 绘制字符
        int len = code.length();
        int fontSize = (int) (height * 0.7);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
        g2.setFont(font);
        int charWidth = width / (len + 1);
        int baseY = (int) (height * 0.75);
        for (int i = 0; i < len; i++) {
            char c = code.charAt(i);
            g2.setColor(randomColor(50, 150));
            double angle = (random.nextDouble() - 0.5) * (Math.PI / 6);
            int x = (i + 1) * charWidth - (fontSize / 2);
            g2.rotate(angle, x, baseY);
            g2.drawString(String.valueOf(c), x, baseY);
            g2.rotate(-angle, x, baseY);
        }

        // 边框
        g2.setColor(new Color(180, 180, 180));
        g2.drawRect(0, 0, width - 1, height - 1);

        g2.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return DATA_URI_PREFIX + base64;
        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    private static Color randomColor(int min, int max) {
        Random random = new Random();
        int r = min + random.nextInt(Math.max(1, max - min));
        int g = min + random.nextInt(Math.max(1, max - min));
        int b = min + random.nextInt(Math.max(1, max - min));
        return new Color(r, g, b);
    }
}






