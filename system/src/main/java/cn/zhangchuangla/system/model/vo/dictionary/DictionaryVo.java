package cn.zhangchuangla.system.model.vo.dictionary;

import cn.zhangchuangla.common.base.BaseVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典", description = "字典表")
public class DictionaryVo extends BaseVO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String name;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
}
