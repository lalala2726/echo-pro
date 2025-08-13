package cn.zhangchuangla.system.core.converter;

import cn.zhangchuangla.system.core.enums.MenuTypeEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/13 14:38
 */
@Component
public class MenuTypeConverter implements Converter<String, MenuTypeEnum> {
    @Override
    public MenuTypeEnum convert(@NotNull String source) {
        return MenuTypeEnum.fromValue(source);
    }
}
