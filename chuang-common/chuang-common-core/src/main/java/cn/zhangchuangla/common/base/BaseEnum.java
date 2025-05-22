package cn.zhangchuangla.common.base;

import cn.hutool.core.util.ObjectUtil;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

/**
 * 枚举通用接口
 *
 * @param <T> 值的类型
 */
public interface BaseEnum<T> {

    /**
     * 根据值获取枚举
     *
     * @param value 枚举值
     * @param clazz 枚举类
     * @return 匹配的枚举或 null
     */
    static <E extends Enum<E> & BaseEnum<?>> E getEnumByValue(Object value, Class<E> clazz) {
        Objects.requireNonNull(value);
        return findEnum(clazz, e -> ObjectUtil.equal(e.getValue(), value));
    }

    /**
     * 根据值获取枚举标签
     *
     * @param value 枚举值
     * @param clazz 枚举类
     * @return 匹配的标签或 null
     */
    static <E extends Enum<E> & BaseEnum<?>> String getLabelByValue(Object value, Class<E> clazz) {
        return Optional.ofNullable(getEnumByValue(value, clazz))
                .map(BaseEnum::getLabel)
                .orElse(null);
    }

    /**
     * 根据标签获取枚举值
     *
     * @param label 标签文本
     * @param clazz 枚举类
     * @return 匹配的值或 null
     */
    static <E extends Enum<E> & BaseEnum<?>> Object getValueByLabel(String label, Class<E> clazz) {
        Objects.requireNonNull(label);
        return Optional.ofNullable(findEnum(clazz, e -> ObjectUtil.equal(e.getLabel(), label)))
                .map(BaseEnum::getValue)
                .orElse(null);
    }

    /**
     * 查找匹配的枚举
     *
     * @param clazz     枚举类
     * @param predicate 匹配条件
     * @return 匹配的枚举或 null
     */
    private static <E extends Enum<E> & BaseEnum<?>> E findEnum(Class<E> clazz, java.util.function.Predicate<E> predicate) {
        return EnumSet.allOf(clazz).stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取枚举的值
     */
    T getValue();

    /**
     * 获取枚举的标签
     */
    String getLabel();
}
