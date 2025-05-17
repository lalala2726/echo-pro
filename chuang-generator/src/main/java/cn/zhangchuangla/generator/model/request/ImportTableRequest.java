package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 导入表请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "导入表请求")
public class ImportTableRequest {

    /**
     * 表名称列表
     */
    @NotEmpty(message = "表名称不能为空")
    @Schema(description = "表名称列表")
    private List<String> tableNames;

    /**
     * 生成包路径
     */
    @Schema(description = "生成包路径", example = "cn.zhangchuangla.project")
    private String packageName;

    /**
     * 生成模块名
     */
    @Schema(description = "生成模块名", example = "system")
    private String moduleName;

    /**
     * 使用的模板（crud, tree, master_child）
     */
    @Schema(description = "使用的模板", example = "crud")
    private String tplCategory;
}