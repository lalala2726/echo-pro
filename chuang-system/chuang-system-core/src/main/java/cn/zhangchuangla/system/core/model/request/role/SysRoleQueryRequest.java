package cn.zhangchuangla.system.core.model.request.role;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 角色参数,用户查询角色列表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "角色列表查询请求对象", description = "角色列表查询请求对象")
public class SysRoleQueryRequest extends BasePageRequest {

    /**
     * 角色名
     */
    @Schema(description = "角色名", example = "管理员", type = "string")
    private String roleName;

    /**
     * 角色权限标识
     */
    @Schema(description = "角色权限标识", example = "admin", type = "string")
    private String roleKey;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1", type = "string")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统默认角色", type = "string")
    private String remark;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2023-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2023-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endTime;

}
