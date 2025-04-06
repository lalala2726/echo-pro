package cn.zhangchuangla.common.enums;

/**
 * 文件内容类型枚举
 * <p>
 * 用于映射文件扩展名到对应的 MIME 类型。
 * </p>
 *
 * @author Chuang
 */
public enum ContentType {
    TXT("text/plain"),
    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    JSON("application/json"),
    XML("application/xml"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    PDF("application/pdf"),
    MP4("video/mp4"),
    MP3("audio/mpeg"),
    ZIP("application/zip"),
    RAR("application/x-rar-compressed"),
    EXE("application/octet-stream"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPT("application/vnd.ms-powerpoint"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    OTHER("application/octet-stream");

    /**
     * MIME 类型字符串
     */
    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 根据文件扩展名获取对应的 ContentType 枚举
     *
     * @param extension 文件扩展名（不区分大小写）
     * @return 对应的 ContentType 枚举
     * @throws IllegalArgumentException 如果扩展名不受支持，则抛出异常
     */
    public static ContentType fromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            throw new IllegalArgumentException("文件扩展名不能为空");
        }

        String lowerExt = extension.toLowerCase();
        for (ContentType type : values()) {
            if (type.name().equalsIgnoreCase(lowerExt) ||
                    (type == JPEG && lowerExt.equals("jpg"))) {
                return type;
            }
        }
        return OTHER;
    }

    /**
     * 获取 MIME 类型
     *
     * @return MIME 类型字符串
     */
    public String getMimeType() {
        return mimeType;
    }

}
