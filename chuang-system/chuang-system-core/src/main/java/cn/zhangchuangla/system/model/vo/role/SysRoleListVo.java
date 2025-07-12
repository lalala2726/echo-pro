package cn.zhangchuangla.system.model.vo.role;

import cn.zhangchuangla.common.excel.annotation.Excel;
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
    @Excel(name = "ID", sort = 1, width = 20)
    private Long id;

    /**
     * 角色名
     */
    @Schema(description = "角色名")
    @Excel(name = "角色名", sort = 2, width = 20)
    private String roleName;

    /**
     * 角色权限标识
     */
    @Schema(description = "角色权限标识")
    @Excel(name = "角色权限标识", sort = 3, width = 20)
    private String roleKey;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Excel(name = "备注", sort = 4, width = 20)
    private String remark;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @Excel(name = "状态", dictType = "system_common_status", sort = 5, width = 10)
    private Integer status;

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
    @Excel(name = "创建时间", sort = 6, width = 20, dateFormat = "yyyy-MM-dd")
    private Date createTime;


}
