package cn.zhangchuangla.system.model.vo.dept;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/13 21:21
 */
@Schema(name = "部门树")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeptTree {

    /**
     * 部门ID
     */
    @Schema(name = "部门ID")
    private Long id;

    /**
     * 部门名称
     */
    @Schema(name = "部门名称")
    private String label;


    /**
     * 子部门
     */
    @Schema(name = "子部门")
    private List<DeptTree> children;
}
