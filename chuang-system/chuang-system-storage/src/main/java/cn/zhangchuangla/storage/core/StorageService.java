package cn.zhangchuangla.storage.core;

import cn.zhangchuangla.storage.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Core interface for storage operations.
 *
 * @author Chuang
 */
public interface StorageService {

    /**
     * Uploads a file.
     *
     * @param file    The multipart file to upload.
     * @param subPath Optional sub-path/directory within the main storage where the file should be stored.
     * @return FileInfo object containing details of the uploaded file.
     */
    FileInfo uploadFile(MultipartFile file, String subPath);

    /**
     * Uploads a file from an InputStream.
     *
     * @param inputStream      The InputStream of the file data.
     * @param originalFileName The original name of the file.
     * @param contentType      The MIME type of the file.
     * @param subPath          Optional sub-path/directory.
     * @return FileInfo object containing details of the uploaded file.
     */
    FileInfo uploadFile(InputStream inputStream, String originalFileName, String contentType, String subPath);

    /**
     * Uploads an image file. Handles original and potentially compressed/thumbnail versions.
     *
     * @param file    The image file to upload.
     * @param subPath Optional sub-path/directory.
     * @return FileInfo object, potentially including thumbnail URL/path.
     */
    FileInfo uploadImage(MultipartFile file, String subPath);

    /**
     * Deletes a file.
     *
     * @param relativePath The relative path of the file in the storage.
     * @return true if deletion was successful, false otherwise.
     */
    boolean deleteFile(String relativePath);

    /**
     * Deletes multiple files.
     *
     * @param relativePaths List of relative paths of the files to delete.
     */
    void deleteFiles(List<String> relativePaths);

    /**
     * Retrieves a file as an InputStream.
     *
     * @param relativePath The relative path of the file.
     * @return InputStream of the file, or null if not found.
     */
    InputStream downloadFile(String relativePath);

    /**
     * Gets the public URL of a stored file.
     *
     * @param relativePath The relative path of the file.
     * @return The public URL string.
     */
    String getFileUrl(String relativePath);

    /**
     * Checks if a file exists.
     *
     * @param relativePath The relative path of the file.
     * @return true if the file exists, false otherwise.
     */
    boolean fileExists(String relativePath);

    /**
     * Moves a file to the trash directory if trash is enabled.
     * If trash is not enabled, this might behave like deleteFile or do nothing.
     *
     * @param relativePath The relative path of the file to move to trash.
     * @return FileInfo containing the new path in trash, or null if operation failed/not applicable.
     */
    FileInfo moveToTrash(String relativePath);

    /**
     * Restores a file from the trash directory.
     *
     * @param trashPath The path of the file in the trash directory.
     * @return FileInfo containing the restored path, or null if operation failed.
     */
    FileInfo restoreFromTrash(String trashPath);

}
