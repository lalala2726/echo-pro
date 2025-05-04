package cn.zhangchuangla.system.model.vo.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统字典项表
 */
@Data
@Schema(description = "系统字典项列表视图对象")
public class SysDictItemListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", format = "int64")
    private Long id;

    /**
     * 所属字典类型编码
     */
    @Schema(description = "所属字典类型编码", type = "string")
    private String dictType;

    /**
     * 字典项名称
     */
    @Schema(description = "字典项名称", type = "string")
    private String itemLabel;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", type = "string")
    private String itemValue;

    /**
     * 排序值
     */
    @Schema(description = "排序值", type = "integer")
    private Integer sort;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用")
    private String status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string")
    private Date createTime;


}