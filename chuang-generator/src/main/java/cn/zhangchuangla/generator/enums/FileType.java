package cn.zhangchuangla.generator.enums;

import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
public enum FileType {
    ENTITY("entity", "实体类"),
    MAPPER("mapper", "数据访问层"),
    SERVICE("service", "服务接口"),
    SERVICE_IMPL("serviceImpl", "服务实现"),
    CONTROLLER("controller", "控制器"),
    MAPPER_XML("mapperXml", "XML映射文件"),
    VO("vo", "视图对象"),
    LIST_VO("listVo", "列表视图对象"),
    REQUEST("request", "请求对象"),
    ADD_REQUEST("addRequest", "添加请求对象"),
    UPDATE_REQUEST("updateRequest", "更新请求对象"),
    OTHER("other", "其他文件");

    private final String code;
    private final String desc;

    FileType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
