package cn.zhangchuangla.app.model.entity.system;

import cn.zhangchuangla.app.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_role")
@Data
public class SysRole extends BaseEntity {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

}
