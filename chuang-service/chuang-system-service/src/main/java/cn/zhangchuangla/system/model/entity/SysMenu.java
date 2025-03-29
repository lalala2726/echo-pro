package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_menu")
@Data
public class SysMenu extends BaseEntity {

    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 是否外链（0是 1否）
     */
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private Object menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private Object visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    private Object status;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

}