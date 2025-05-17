package cn.zhangchuangla.storage.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

/**
 * Utility class for generating and manipulating storage paths.
 *
 * @author Chuang
 */
public class StoragePathUtils {

    private static final String URL_SEPARATOR = "/";

    /**
     * Generates a unique file path including subdirectories based on date and original filename.
     *
     * @param subPath        Optional base sub-path (e.g., "user-uploads", "images"). Can be null or empty.
     * @param uniqueFileName The unique name of the file (e.g., with timestamp/UUID).
     * @return A relative path string, e.g., "user-uploads/2023/05/17/uniqueFileName.jpg" or "2023/05/17/uniqueFileName.jpg".
     */
    public static String generatePath(String subPath, String uniqueFileName) {
        java.time.LocalDate today = java.time.LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        StringBuilder pathBuilder = new StringBuilder();
        if (StringUtils.hasText(subPath)) {
            pathBuilder.append(sanitizePath(subPath)).append(URL_SEPARATOR);
        }
        pathBuilder.append(year).append(URL_SEPARATOR);
        pathBuilder.append(month).append(URL_SEPARATOR);
        pathBuilder.append(day).append(URL_SEPARATOR);
        pathBuilder.append(sanitizePath(uniqueFileName));

        return pathBuilder.toString();
    }

    /**
     * Generates a path for an original image.
     *
     * @param uniqueFileName The unique name of the file.
     * @return Path like "images/original/2023/05/17/uniqueFileName.jpg"
     */
    public static String generateOriginalImagePath(String uniqueFileName) {
        return generatePath("images/original", uniqueFileName);
    }

    /**
     * Generates a path for a compressed/thumbnail image.
     *
     * @param uniqueFileName The unique name of the file.
     * @return Path like "images/thumbnail/2023/05/17/uniqueFileName.jpg"
     */
    public static String generateThumbnailImagePath(String uniqueFileName) {
        return generatePath("images/thumbnail", uniqueFileName);
    }


    /**
     * Sanitizes a path segment to prevent directory traversal issues.
     * Removes leading/trailing slashes and normalizes path separators.
     *
     * @param pathSegment The path segment to sanitize.
     * @return The sanitized path segment.
     */
    private static String sanitizePath(String pathSegment) {
        if (!StringUtils.hasText(pathSegment)) {
            return "";
        }
        // Normalize path separators to forward slashes
        String normalized = FilenameUtils.normalizeNoEndSeparator(pathSegment.replace("\\", URL_SEPARATOR), true);
        // Remove leading slashes to prevent absolute path interpretation in relative context
        if (normalized.startsWith(URL_SEPARATOR)) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    /**
     * Concatenates a base URL/domain with a relative path to form a full URL.
     *
     * @param baseOrDomain The base URL or domain (e.g., "https://cdn.example.com", "/resources").
     * @param relativePath The relative path of the file (e.g., "images/pic.jpg").
     * @return The full URL.
     */
    public static String concatUrl(String baseOrDomain, String relativePath) {
        String sanitizedBase = baseOrDomain != null ? baseOrDomain.trim() : "";
        String sanitizedRelativePath = relativePath != null ? relativePath.trim() : "";

        // Remove trailing slash from base
        if (sanitizedBase.endsWith(URL_SEPARATOR)) {
            sanitizedBase = sanitizedBase.substring(0, sanitizedBase.length() - 1);
        }
        // Remove leading slash from relative path
        if (sanitizedRelativePath.startsWith(URL_SEPARATOR)) {
            sanitizedRelativePath = sanitizedRelativePath.substring(1);
        }
        return sanitizedBase + URL_SEPARATOR + sanitizedRelativePath;
    }
}
