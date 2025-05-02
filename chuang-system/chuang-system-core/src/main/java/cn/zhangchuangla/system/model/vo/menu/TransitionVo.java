package cn.zhangchuangla.system.model.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 页面过渡动画配置
 *
 * @author Chuang
 */
@Schema(description = "页面过渡动画配置")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransitionVo {

    /**
     * 当前页面动画
     */
    private String name;

    /**
     * 当前页面进场动画
     */
    private String enterTransition;

    /**
     * 当前页面离场动画
     */
    private String leaveTransition;
}