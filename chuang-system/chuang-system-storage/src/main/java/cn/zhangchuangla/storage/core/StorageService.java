package cn.zhangchuangla.storage.core;

import cn.zhangchuangla.storage.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 存储操作的核心接口。
 *
 * @author Chuang
 */
public interface StorageService {

    /**
     * 上传文件。
     *
     * @param file    要上传的 multipart 文件。
     * @param subPath 可选的子路径/目录，用于在主存储位置内存储文件。
     * @return 包含上传文件详细信息的 FileInfo 对象。
     */
    FileInfo uploadFile(MultipartFile file, String subPath);

    /**
     * 通过 InputStream 上传文件。
     *
     * @param inputStream      文件数据的 InputStream。
     * @param originalFileName 文件的原始名称。
     * @param contentType      文件的 MIME 类型。
     * @param subPath          可选的子路径/目录。
     * @return 包含上传文件详细信息的 FileInfo 对象。
     */
    FileInfo uploadFile(InputStream inputStream, String originalFileName, String contentType, String subPath);

    /**
     * 上传图片文件。处理原图以及可能压缩后的缩略图。
     *
     * @param file    要上传的图片文件。
     * @param subPath 可选的子路径/目录。
     * @return FileInfo 对象，可能包含缩略图的 URL 或路径。
     */
    FileInfo uploadImage(MultipartFile file, String subPath);

    /**
     * 删除文件。
     *
     * @param relativePath 文件在存储中的相对路径。
     * @return 如果删除操作成功返回 true，否则返回 false。
     */
    boolean deleteFile(String relativePath);

    /**
     * 删除多个文件。
     *
     * @param relativePaths 要删除文件的相对路径列表。
     */
    void deleteFiles(List<String> relativePaths);

    /**
     * 获取文件的 InputStream。
     *
     * @param relativePath 文件的相对路径。
     * @return 文件的 InputStream，如果未找到则返回 null。
     */
    InputStream downloadFile(String relativePath);

    /**
     * 获取已存储文件的公共 URL。
     *
     * @param relativePath 文件的相对路径。
     * @return 公共 URL 字符串。
     */
    String getFileUrl(String relativePath);

    /**
     * 检查文件是否存在。
     *
     * @param relativePath 文件的相对路径。
     * @return 如果文件存在返回 true，否则返回 false。
     */
    boolean fileExists(String relativePath);

    /**
     * 将文件移动到回收站目录（如果启用了回收站功能）。
     * 如果未启用回收站，此方法的行为可能与 deleteFile 相同或无任何操作。
     *
     * @param relativePath 要移动到回收站的文件的相对路径。
     * @return 包含回收站中新路径的 FileInfo 对象，如果操作失败或不适用则返回 null。
     */
    FileInfo moveToTrash(String relativePath);

    /**
     * 从回收站恢复文件。
     *
     * @param trashPath 回收站中文件的路径。
     * @return 包含恢复后路径的 FileInfo 对象，如果操作失败则返回 null。
     */
    FileInfo restoreFromTrash(String trashPath);

}
