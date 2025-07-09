package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 菜单表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_menu")
@Data
public class SysMenu extends BaseEntity {
    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 标题
     */
    private String title;

    /**
     * 路径
     */
    private String path;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 激活路径
     */
    private String activePath;

    /**
     * 激活图标
     */
    private String activeIcon;

    /**
     * 图标
     */
    private String icon;

    /**
     * 组件
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 徽标类型
     */
    private String badgeType;

    /**
     * 徽标
     */
    private String badge;

    /**
     * 徽标颜色
     */
    private String badgeVariants;

    /**
     * 是否缓存
     */
    private Integer keepAlive;

    /**
     * 是否固定标签页
     */
    private Integer affixTab;

    /**
     * 隐藏菜单
     */
    private Integer hideInMenu;

    /**
     * 隐藏子菜单
     */
    private Integer hideChildrenInMenu;

    /**
     * 隐藏在面包屑中
     */
    private Integer hideInBreadcrumb;

    /**
     * 隐藏在标签页中
     */
    private Integer hideInTab;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

}
