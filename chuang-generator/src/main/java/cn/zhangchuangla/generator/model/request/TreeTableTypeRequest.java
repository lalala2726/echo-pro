package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/24 01:21
 */
@Data
@Schema(name = "树形结构类型请求类", description = "用于修改树形结构类型信息")
public class TreeTableTypeRequest {

    /**
     * 树表编码字段
     */
    @Schema(description = "树表编码字段", example = "parent_id")
    private String treeCode;

    /**
     * 树表名称字段
     */
    @Schema(description = "树表名称字段", example = "user_name")
    private String treeName;

    /**
     * 树表父编码字段
     */
    @Schema(description = "树表父编码字段", example = "parent_id")
    private String treeParentCode;
}
