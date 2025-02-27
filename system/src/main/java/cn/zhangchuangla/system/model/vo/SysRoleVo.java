package cn.zhangchuangla.system.model.vo;

import cn.zhangchuangla.common.base.BaseVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "角色VO", description = "返回前端展示的数据")
public class SysRoleVo extends BaseVO {
    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    private Long roleId;

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
    private Integer roleSort;


}
