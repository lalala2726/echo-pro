package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.storage.constant.StorageConstants;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 基于 {@link InputStream}/{@link OutputStream} 的高效图片处理工具类。
 * <p>
 * 设计要点：
 * <ul>
 *   <li>全部方法以流为入口与出口，避免在内存中反复复制大块 byte[]。</li>
 *   <li>调用方可决定是否复用流（例如从网络直读、直写至对象存储）。</li>
 *   <li>对 CPU 与内存敏感的场景，仅在必要时才读取 {@link BufferedImage} 进行尺寸判定；
 *       其余操作直接依赖 Thumbnailator 的流式管线。</li>
 *   <li>所有方法无状态（static），线程安全，可在并发环境下复用。</li>
 * </ul>
 *
 * @author Chuang
 * @since 2025/06/30
 */
@Slf4j
public final class ImageStreamUtils {

    private ImageStreamUtils() {
    }

    /**
     * 等比例压缩图片：若原尺寸已小于目标尺寸，仅做质量压缩。
     *
     * @param in        原图输入流，方法内不会关闭
     * @param out       结果输出流，方法内不会关闭
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param quality   压缩质量 (0.0-1.0)
     * @param fileName  文件名，用于获取格式
     */
    public static void compress(InputStream in, OutputStream out, int maxWidth, int maxHeight, float quality, String fileName) throws IOException {
        // 使用 BufferedInputStream 来支持 mark/reset，避免消耗流
        InputStream bufferedIn = in.markSupported() ? in : new BufferedInputStream(in);
        // 标记流的开始位置
        bufferedIn.mark(Integer.MAX_VALUE);

        BufferedImage origin = ImageIO.read(bufferedIn);
        if (origin == null) {
            throw new IOException("Cannot read image data from InputStream. It might be empty or in an unsupported format.");
        }
        int ow = origin.getWidth();
        int oh = origin.getHeight();

        // 重置流，以便 Thumbnails 可以重新读取它
        bufferedIn.reset();

        String format = getFormatName(fileName);

        net.coobird.thumbnailator.Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(bufferedIn).outputFormat(format);

        if (ow <= maxWidth && oh <= maxHeight) {
            // 尺寸已经小于目标，仅进行质量压缩
            builder.scale(1.0).outputQuality(safeQuality(quality));
        } else {
            // 按比例缩放
            builder.size(maxWidth, maxHeight).keepAspectRatio(true).outputQuality(safeQuality(quality));
        }
        builder.toOutputStream(out);
    }

    /**
     * 等比例缩放
     */
    public static void resize(InputStream in, OutputStream out, int targetW, int targetH, float quality, String fileName) throws IOException {
        Thumbnails.of(in)
                .size(targetW, targetH)
                .keepAspectRatio(true)
                .outputQuality(safeQuality(quality))
                .outputFormat(getFormatName(fileName))
                .toOutputStream(out);
    }

    /**
     * 裁剪
     */
    public static void crop(InputStream in, OutputStream out, int cropW, int cropH, Positions position, String fileName) throws IOException {
        Thumbnails.of(in)
                .sourceRegion(position, cropW, cropH)
                .size(cropW, cropH)
                .keepAspectRatio(false)
                .outputFormat(getFormatName(fileName))
                .toOutputStream(out);
    }

    /**
     * 添加水印
     */
    public static void addWatermark(InputStream imageIn, InputStream watermarkIn, OutputStream out, Positions position, float opacity, String fileName) throws IOException {
        BufferedImage watermark = ImageIO.read(watermarkIn);
        Thumbnails.of(imageIn)
                .scale(1.0)
                .watermark(position, watermark, opacity)
                .outputFormat(getFormatName(fileName))
                .toOutputStream(out);
    }

    /**
     * 旋转
     */
    public static void rotate(InputStream in, OutputStream out, double angle, String fileName) throws IOException {
        Thumbnails.of(in)
                .scale(1.0)
                .rotate(angle)
                .outputFormat(getFormatName(fileName))
                .toOutputStream(out);
    }

    /**
     * 格式转换
     */
    public static void convertFormat(InputStream in, OutputStream out, String formatName) throws IOException {
        Thumbnails.of(in)
                .scale(1.0)
                .outputFormat(formatName)
                .toOutputStream(out);
    }

    /**
     * 判断文件是否图片
     */
    public static boolean isImage(String imageExtension) {
        if (imageExtension == null || imageExtension.isEmpty()) {
            return false;
        }
        // 统一转换为小写，并移除可能存在的前导点
        String normalizedExtension = imageExtension.toLowerCase().startsWith(".")
                ? imageExtension.substring(1)
                : imageExtension;
        // 检查后缀是否在列表中（列表中的后缀也应该是不带点的）
        return StorageConstants.imageSuffix.stream()
                .map(suffix -> suffix.startsWith(".") ? suffix.substring(1) : suffix)
                .anyMatch(suffix -> suffix.equalsIgnoreCase(normalizedExtension));
    }

    /**
     * 统一限制质量范围，避免异常
     */
    private static float safeQuality(float quality) {
        return (quality < 0.0f) ? 0.0f : Math.min(quality, 1.0f);
    }

    /**
     * 从文件名中获取格式名称（文件扩展名）。
     *
     * @param fileName 文件名
     * @return 格式名称 (e.g., "jpg", "png")
     */
    private static String getFormatName(String fileName) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                String ext = fileName.substring(dotIndex + 1);
                if (isSupportedFormat(ext)) {
                    return ext;
                }
            }
        }
        // 默认为 jpg 格式
        return "jpg";
    }

    /**
     * 检查格式是否受支持
     */
    private static boolean isSupportedFormat(String format) {
        if (format == null || format.trim().isEmpty()) {
            return false;
        }
        String lowerFormat = format.toLowerCase();
        return "jpg".equals(lowerFormat) || "jpeg".equals(lowerFormat) || "png".equals(lowerFormat) || "gif".equals(lowerFormat);
    }
}
