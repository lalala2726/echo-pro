package cn.zhangchuangla.system.model.vo.menu;

import cn.zhangchuangla.common.config.jackson.CustomBooleanSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 路由配置信息
 *
 * @author zhangchuang
 */
@Data
@Schema(description = "前端路由信息")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String name;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String path;

    /**
     * 是否隐藏路由
     */
    @Schema(description = "是否隐藏路由", defaultValue = "false")
    @JsonSerialize(using = CustomBooleanSerializer.class)
    private boolean hidden;

    /**
     * 重定向地址
     */
    @Schema(description = "重定向地址")
    private String redirect;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由参数
     */
    @Schema(description = "路由参数")
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式
     */
    @Schema(description = "是否总是显示", defaultValue = "false")
    @JsonSerialize(using = CustomBooleanSerializer.class)
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    @Schema(description = "路由元数据信息")
    private MetaVo meta;

    /**
     * 子路由
     */
    @Schema(description = "子路由")
    private List<RouterVo> children;
}
