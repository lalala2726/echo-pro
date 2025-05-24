package cn.zhangchuangla.generator.enums;

import lombok.Getter;

/**
 * 模板类型枚举
 *
 * @author Chuang
 * @since 2025-01-23
 */
@Getter
public enum TemplateTypeEnum {

  /**
   * 单表CRUD
   */
  CRUD("crud", "单表CRUD"),

  /**
   * 树表
   */
  TREE("tree", "树表"),

  /**
   * 主子表
   */
  SUB("sub", "主子表");

  private final String code;
  private final String description;

  TemplateTypeEnum(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * 根据代码获取枚举
   *
   * @param code 代码
   * @return 枚举
   */
  public static TemplateTypeEnum getByCode(String code) {
    for (TemplateTypeEnum type : values()) {
      if (type.getCode().equals(code)) {
        return type;
      }
    }
    return CRUD; // 默认返回CRUD
  }
}