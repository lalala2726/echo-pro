package cn.zhangchuangla.system.model.request.dict;

import cn.zhangchuangla.common.base.BasePageRequest;
import cn.zhangchuangla.common.base.BaseVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典项表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "系统字典项列表请求对象")
public class SysDictItemListRequest extends BasePageRequest {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    private Long id;

    /**
     * 所属字典类型编码
     */
    @Schema(description = "所属字典类型编码", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "system_common_status")
    private String dictType;

    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "正常")
    private String itemLabel;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "0")
    private String itemValue;

    /**
     * 排序值
     */
    @Schema(description = "排序值", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    private String status;


}