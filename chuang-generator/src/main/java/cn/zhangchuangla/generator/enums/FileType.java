package cn.zhangchuangla.generator.enums;

import lombok.Getter;

/**
 * 文件类型枚举
 *
 * @author zhangchuang
 */
@Getter
public enum FileType {
    ENTITY("entity", "实体类", "entity.java"),
    MAPPER("mapper", "数据访问层", "mapper.java"),
    SERVICE("service", "服务接口", "service.java"),
    SERVICE_IMPL("serviceImpl", "服务实现", "serviceImpl.java"),
    CONTROLLER("controller", "控制器", "controller.java"),
    MAPPER_XML("mapperXml", "XML映射文件", "mapper.xml"),
    VO("vo", "视图对象", "vo.java"),
    LIST_VO("listVo", "列表视图对象", "list-vo.java"),
    REQUEST("request", "请求对象", "request.java"),
    ADD_REQUEST("addRequest", "添加请求对象", "add-request.java"),
    UPDATE_REQUEST("updateRequest", "更新请求对象", "update-request.java"),
    OTHER("other", "其他文件", "");

    private final String code;
    private final String desc;
    private final String fileNamePattern;

    FileType(String code, String desc, String fileNamePattern) {
        this.code = code;
        this.desc = desc;
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * 根据文件名获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型
     */
    public static FileType getFileType(String fileName) {
        if (fileName == null) {
            return OTHER;
        }

        // 特殊处理service和serviceImpl的情况
        if (fileName.contains(SERVICE.fileNamePattern) && !fileName.contains(SERVICE_IMPL.fileNamePattern)) {
            return SERVICE;
        }

        for (FileType fileType : FileType.values()) {
            if (fileType != SERVICE && fileType != OTHER && fileName.contains(fileType.fileNamePattern)) {
                return fileType;
            }
        }

        return OTHER;
    }
}
