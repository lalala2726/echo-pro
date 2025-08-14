package cn.zhangchuangla.system.core.model.vo.post;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 岗位表
 *
 * @author Chuang
 */
@Data
@Schema(name = "岗位列表视图对象", description = "用于展示岗位列表的视图对象")
public class SysPostListVo {

    /**
     * 岗位ID
     */
    @Schema(name = "岗位ID", description = "岗位唯一标识符", type = "integer", example = "1")
    @Excel(name = "岗位ID")
    private Long id;

    /**
     * 岗位编码
     */
    @Schema(name = "岗位编码", description = "岗位的编码标识", type = "string", example = "POST001")
    @Excel(name = "岗位编码")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称", description = "岗位的名称", type = "string", example = "系统管理员")
    @Excel(name = "岗位名称")
    private String postName;

    /**
     * 排序
     */
    @Schema(name = "排序", description = "岗位显示顺序", type = "integer", example = "1")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(name = "状态(0-正常,1-停用)", description = "岗位状态，0表示正常，1表示停用", type = "integer", example = "0")
    @Excel(name = "状态(0-正常,1-停用)")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间", description = "岗位创建时间", type = "date")
    @Excel(name = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
