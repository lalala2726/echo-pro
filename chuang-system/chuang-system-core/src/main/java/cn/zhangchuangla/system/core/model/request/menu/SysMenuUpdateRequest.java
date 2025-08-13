package cn.zhangchuangla.system.core.model.request.menu;

import cn.zhangchuangla.system.core.enums.MenuTypeEnum;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 菜单更新请求
 *
 * @author Chuang
 */
@Data
@Schema(name = "菜单更新请求对象", description = "菜单更新请求对象")
public class SysMenuUpdateRequest {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    @Schema(description = "ID", type = "int64", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称", type = "string", example = "System")
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 路径
     */
    @Schema(description = "路径", type = "string", example = "/system")
    @NotEmpty(message = "路径不能为空")
    private String path;

    /**
     * 类型
     */
    @Schema(description = "类型", type = "string", example = "catalog")
    @NotNull(message = "类型不能为空")
    private MenuTypeEnum type;

    /**
     * 状态
     */
    @Schema(description = "状态", type = "integer", example = "1")
    private Integer status;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID", type = "int64", example = "0")
    private Long parentId;

    /**
     * 标题
     */
    @Schema(description = "标题", type = "string", example = "系统菜单")
    @NotEmpty(message = "标题不能为空")
    private String title;

    /**
     * 激活路径
     */
    @Schema(description = "激活路径", type = "string", example = "/system")
    private String activePath;

    /**
     * 图标
     */
    @Schema(description = "图标", type = "string", example = "el-icon-setting")
    private String icon;

    /**
     * 激活图标
     */
    @Schema(description = "激活图标", type = "string", example = "el-icon-setting")
    private String activeIcon;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径", type = "string", example = "system/index")
    private String component;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识", type = "string", example = "system:menu:list")
    private String permission;

    /**
     * 徽标类型
     */
    @Schema(description = "徽标类型", type = "string", example = "primary")
    private String badgeType;

    /**
     * 徽标
     */
    @Schema(description = "徽标", type = "string", example = "1")
    private String badge;

    /**
     * 徽标颜色
     */
    @Schema(description = "徽标颜色", type = "string", example = "red")
    private String badgeVariants;

    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存", type = "boolean", example = "true")
    private Boolean keepAlive;

    /**
     * 是否固定标签
     */
    @Schema(description = "是否固定标签", type = "boolean", example = "true")
    private Boolean affixTab;

    /**
     * 是否在菜单中隐藏
     */
    @Schema(description = "是否在菜单中隐藏", type = "boolean", example = "true")
    private Boolean hideInMenu;

    /**
     * 是否在菜单中隐藏子项
     */
    @Schema(description = "是否在菜单中隐藏子项", type = "boolean", example = "true")
    private Boolean hideChildrenInMenu;

    /**
     * 外部链接地址
     */
    @Schema(description = "外部链接地址", type = "string", example = "https://www.baidu.com")
    @Pattern(regexp = "^https?://.*$", message = "请输入正确的链接地址")
    private String link;

    /**
     * 在面包屑中隐藏
     */
    @Schema(description = "在面包屑中隐藏", type = "boolean", example = "true")
    private Boolean hideInBreadcrumb;

    /**
     * 在标签栏中隐藏
     */
    @Schema(description = "在标签栏中隐藏", type = "boolean", example = "true")
    private Boolean hideInTab;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "integer", example = "1")
    private Integer sort;

    @JsonSetter("type")
    public void setType(String type) {
        this.type = MenuTypeEnum.fromValue(type);
    }


}
