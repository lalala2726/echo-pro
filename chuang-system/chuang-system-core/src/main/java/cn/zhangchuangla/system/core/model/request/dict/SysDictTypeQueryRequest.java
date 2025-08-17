package cn.zhangchuangla.system.core.model.request.dict;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.common.core.entity.base.TimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询请求对象
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典类型查询请求对象", description = "字典类型查询请求对象")
public class SysDictTypeQueryRequest extends BasePageRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64")
    private Long id;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", example = "user_status", type = "string")
    private String dictType;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", example = "用户状态", type = "string")
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "integer")
    private Integer status;


    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private TimeRange createTime;
}
