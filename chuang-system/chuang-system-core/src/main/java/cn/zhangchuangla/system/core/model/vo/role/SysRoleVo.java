package cn.zhangchuangla.system.core.model.vo.role;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @Schema(description = "角色名")
    private String roleName;

    /**
     * 角色权限标识
     */
    @Schema(description = "角色权限标识")
    private String roleKey;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer sort;


}
