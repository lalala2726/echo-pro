package cn.zhangchuangla.system.model.vo.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 角色VO
 *
 * @author Chuang
 */
@Data
@Schema(name = "角色列表视图对象", description = "返回前端展示的数据")
public class SysRoleListVo {

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
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer sort;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;


}
