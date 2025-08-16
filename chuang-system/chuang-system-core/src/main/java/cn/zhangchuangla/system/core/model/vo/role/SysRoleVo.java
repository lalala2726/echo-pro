package cn.zhangchuangla.system.core.model.vo.role;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色VO
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "角色列表视图对象", description = "返回前端展示的数据")
public class SysRoleVo extends BaseVo {


    /**
     * 主键
     */
    @Schema(description = "主键", type = "string", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名
     */
    @Schema(description = "角色名", type = "string", example = "管理员")
    private String roleName;


    /**
     * 角色状态（0正常 1停用）
     */
    @Schema(description = "角色状态（0正常 1停用）", type = "string", allowableValues = {"0", "1"}, example = "0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
     * 角色权限标识
     */
    @Schema(description = "角色权限标识", type = "string", example = "admin")
    private String roleKey;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序", type = "string", example = "1")
    private Integer sort;


}
