package cn.zhangchuangla.storage.async;

import cn.zhangchuangla.storage.utils.ImageStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * 存储模块异步服务
 * 专门处理存储相关的异步任务
 *
 * @author Chuang
 */
@Slf4j
@Service
public class StorageAsyncService {

    /**
     * 异步压缩图片
     *
     * @param originalFilePath   原图文件路径
     * @param compressedFilePath 压缩图文件路径
     * @param maxWidth           最大宽度
     * @param maxHeight          最大高度
     * @param quality            压缩质量
     * @param originalFilename   原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImage(String originalFilePath, String compressedFilePath,
                              int maxWidth, int maxHeight, float quality, String originalFilename) {
        try {
            log.info("开始异步压缩图片: {} -> {}", originalFilePath, compressedFilePath);

            // 确保压缩图目录存在
            File compressedFile = new File(compressedFilePath);
            File parentDir = compressedFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("无法创建压缩图目录: " + parentDir.getAbsolutePath());
            }

            // 执行压缩
            try (InputStream in = new FileInputStream(originalFilePath);
                 OutputStream out = new FileOutputStream(compressedFilePath)) {
                ImageStreamUtils.compress(in, out, maxWidth, maxHeight, quality, originalFilename);
            }

            log.info("异步图片压缩完成: {}", compressedFilePath);
        } catch (Exception e) {
            log.error("异步图片压缩失败: {} -> {}, 错误: {}", originalFilePath, compressedFilePath, e.getMessage(), e);
            // 压缩失败时清理可能创建的空文件
            try {
                File compressedFile = new File(compressedFilePath);
                if (compressedFile.exists()) {
                    FileUtils.deleteQuietly(compressedFile);
                }
            } catch (Exception cleanupException) {
                log.error("清理压缩失败文件时出错: {}", cleanupException.getMessage());
            }
        }
    }
}
