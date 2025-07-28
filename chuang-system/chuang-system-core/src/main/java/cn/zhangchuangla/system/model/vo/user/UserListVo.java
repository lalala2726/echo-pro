package cn.zhangchuangla.system.model.vo.user;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Chuang
 */
@Data
@Schema(name = "用户列表视图对象", description = "用于在列表中展示用户的视图对象")
public class UserListVo {

    /**
     * ID
     */
    @Schema(description = "用户ID")
    @Excel(name = "用户ID", sort = 1, width = 15)
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @Excel(name = "用户名", sort = 2, width = 20)
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    @Excel(name = "昵称", sort = 3, width = 15)
    private String nickname;

    /**
     * 性别
     */
    @Schema(description = "性别")
    @Excel(name = "性别", sort = 4, width = 10, dictKey = "gender")
    private Integer gender;


    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Excel(name = "邮箱", sort = 5, width = 25)
    private String email;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间", sort = 6, width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "更新时间", sort = 7, width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @Excel(name = "手机号", sort = 8, width = 15)
    private String phone;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @Excel(name = "创建人", sort = 9, width = 15)
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    @Excel(name = "更新人", sort = 10, width = 15)
    private String updateBy;


    /**
     * 状态
     */
    @Schema(description = "状态")
    @Excel(name = "状态", sort = 11, width = 10)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Excel(name = "备注", sort = 12, width = 30)
    private String remark;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @Excel(name = "部门名称", sort = 13, width = 20)
    private String deptName;


}
