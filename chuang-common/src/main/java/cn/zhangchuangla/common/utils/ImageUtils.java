package cn.zhangchuangla.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 图片处理工具类
 *
 * @author zhangchuang
 * Created on 2025/3/22
 */
@Slf4j
public class ImageUtils {

    /**
     * 支持的图片MIME类型集合
     */
    public static final Set<String> IMAGE_EXTENSION = new HashSet<>(Arrays.asList(
            "jpeg", "png", "gif", "bmp", "webp", "jpg", "tiff", "svg+xml"
    ));

    /**
     * 压缩图片
     *
     * @param imageBytes 原图片字节数组
     * @param maxWidth   最大宽度
     * @param maxHeight  最大高度
     * @param quality    压缩质量(0.0-1.0)
     * @return 压缩后的图片字节数组
     * @throws IOException 压缩过程中发生IO异常
     */
    public static byte[] compressImage(byte[] imageBytes, int maxWidth, int maxHeight, float quality) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        // 读取原图
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (originalImage == null) {
            throw new IOException("Cannot read image data");
        }

        // 获取原图尺寸
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 如果原图尺寸已经小于目标尺寸，不需要缩放
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            // 仍然需要压缩质量
            return compressImageQuality(imageBytes, quality);
        }

        // 计算缩放比例，保持宽高比
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // 创建缩放后的图片
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // 设置渲染质量
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制图片
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // 保存图片
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 判断图片格式
        String formatName = "jpg"; // 默认jpg格式

        // 尝试保存为原格式
        ImageIO.write(resizedImage, formatName, outputStream);

        // 应用质量压缩
        byte[] resizedBytes = outputStream.toByteArray();
        return compressImageQuality(resizedBytes, quality);
    }

    /**
     * 压缩图片质量
     *
     * @param imageBytes 图片字节数组
     * @param quality    压缩质量(0.0-1.0)
     * @return 压缩后的图片字节数组
     */
    private static byte[] compressImageQuality(byte[] imageBytes, float quality) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 将BufferedImage写入ByteArrayOutputStream
        ImageIO.write(image, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 判断文件是否为图片类型
     *
     * @param imageExtension 文件后缀名
     * @return 是否为图片
     */
    public static boolean isImage(String imageExtension) {
        log.info("文件后缀名: {}", imageExtension);
        boolean result = imageExtension != null && IMAGE_EXTENSION.contains(imageExtension.toLowerCase());
        log.info("判断结果: {}", result);
        return result;
    }

    /**
     * 压缩图片（等比例缩放）
     *
     * @param imageBytes 原图片字节数组
     * @param width      目标宽度
     * @param height     目标高度
     * @param quality    图片质量 0.0-1.0
     * @return 压缩后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] resize(byte[] imageBytes, int width, int height, float quality) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(width, height)
                .keepAspectRatio(true)
                .outputQuality(quality)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 按照指定尺寸裁剪图片
     *
     * @param imageBytes 原图片字节数组
     * @param width      裁剪宽度
     * @param height     裁剪高度
     * @param position   裁剪位置，使用 Positions 类的常量，如 Positions.CENTER
     * @return 裁剪后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] crop(byte[] imageBytes, int width, int height, Positions position) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .sourceRegion(position, width, height)
                .size(width, height)
                .keepAspectRatio(false)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 添加水印
     *
     * @param imageBytes     原图片字节数组
     * @param watermarkBytes 水印图片字节数组
     * @param position       水印位置，使用 Positions 类的常量，如 Positions.BOTTOM_RIGHT
     * @param opacity        水印透明度 0.0-1.0
     * @return 添加水印后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] addWatermark(byte[] imageBytes, byte[] watermarkBytes, Positions position, float opacity) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        ByteArrayInputStream imageInputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayInputStream watermarkInputStream = new ByteArrayInputStream(watermarkBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage watermarkImage = ImageIO.read(watermarkInputStream);

        Thumbnails.of(imageInputStream)
                .scale(1.0)
                .watermark(position, watermarkImage, opacity)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 旋转图片
     *
     * @param imageBytes 原图片字节数组
     * @param angle      旋转角度，正数为顺时针，负数为逆时针
     * @return 旋转后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] rotate(byte[] imageBytes, double angle) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .scale(1.0)
                .rotate(angle)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 转换图片格式
     *
     * @param imageBytes 原图片字节数组
     * @param formatName 目标格式名称，如 "jpg", "png", "gif" 等
     * @return 转换格式后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] convertFormat(byte[] imageBytes, String formatName) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return new byte[0];
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .scale(1.0)
                .outputFormat(formatName)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
