package cn.zhangchuangla.system.model.request.dict;

import cn.zhangchuangla.common.base.BasePageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典列表请求类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典列表请求类")
public class SysDictListRequest extends BasePageRequest {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    /**
     * 状态(0:正常;1:禁用)
     */
    @Schema(description = "状态(0:正常;1:禁用)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;


}
