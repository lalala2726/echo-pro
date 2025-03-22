package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 文件相关工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/18 15:27
 */
public class FileUtils {
    /**
     * 校验文件有效性
     */
    public static boolean validateFile(MultipartFile file) {
        return file != null && !file.isEmpty() && file.getOriginalFilename() != null;
    }

    /**
     * 获取文件扩展名（带点）
     */
    public static String getFileExtension(String fileName) {
        return (fileName != null && fileName.contains(".")) ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    /**
     * 根据文件类型返回合适的 Content-Type
     */
    public static String determineContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.equals("application/octet-stream")) {
            String ext = getFileExtension(file.getOriginalFilename()).toLowerCase();
            return switch (ext) {
                case ".jpg", ".jpeg" -> "image/jpeg";
                case ".png" -> "image/png";
                case ".gif" -> "image/gif";
                case ".pdf" -> "application/pdf";
                case ".mp4" -> "video/mp4";
                default -> "application/octet-stream";
            };
        }
        return contentType;
    }


    /**
     * 生成唯一文件名,使用UUID和时间戳结合
     */
    public static String generateUUID() {
        return System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

    /**
     * 计算文件的MD5值
     *
     * @param fileBytes 文件字节数组
     * @return 返回MD5值
     */
    public static String calculateMD5(byte[] fileBytes) {
        if (fileBytes == null) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "文件不能为空！");
        }
        try {
            // 获取MD5算法的MessageDigest实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算MD5摘要
            byte[] digest = md.digest(fileBytes);
            // 将byte数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 将每个byte转为两位十六进制数
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "计算MD5值失败：" + e.getMessage());
        }
    }
}
