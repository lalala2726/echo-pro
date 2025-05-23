package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


/**
 * 代码生成业务表更新请求
 *
 * @author Chuang
 */
@Data
@Schema(name = "代码生成业务表更新请求", description = "用于更新低代码业务表信息")
public class GenTableUpdateRequest {

    /**
     * 编号
     */
    @NotNull(message = "表ID不能为空")
    @Schema(description = "编号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tableId;

    /**
     * 表名称
     */
    @Schema(description = "表名称", example = "user_info")
    private String tableName;

    /**
     * 表描述
     */
    @Schema(description = "表描述", example = "用户信息表")
    private String tableComment;

    /**
     * 实体类名称
     */
    @Schema(description = "实体类名称", example = "UserInfo")
    @Size(max = 100, message = "实体类名称长度不能超过100个字符")
    private String className;

    /**
     * 生成包路径
     */
    @Schema(description = "生成包路径", example = "com.example.project")
    @Size(max = 200, message = "生成包路径长度不能超过200个字符")
    private String packageName;

    /**
     * 生成模块名
     */
    @Schema(description = "生成模块名", example = "user")
    @Size(max = 30, message = "生成模块名长度不能超过30个字符")
    private String moduleName;

    /**
     * 生成业务名
     */
    @Schema(description = "生成业务名", example = "userInfo")
    @Size(max = 30, message = "生成业务名长度不能超过30个字符")
    private String businessName;

    /**
     * 生成功能名
     */
    @Schema(description = "生成功能名", example = "用户管理")
    @Size(max = 50, message = "生成功能名长度不能超过50个字符")
    private String functionName;

    /**
     * 生成功能作者
     */
    @Schema(description = "生成功能作者", example = "张强")
    @Size(max = 50, message = "作者名称长度不能超过50个字符")
    private String functionAuthor;

    /**
     * 生成模板类型
     */
    @Schema(description = "生成模板类型", example = "crud")
    private String tplCategory;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "这是一个示例备注")
    private String remark;

    /**
     * 代码生成业务表字段信息
     */
    @Schema(description = "代码生成业务表字段信息")
    private List<ColumnUpdateRequest> columns;

}
