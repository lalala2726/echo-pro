package cn.zhangchuangla.system.message.converter;

import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/10 15:04
 */
@Component
public class MessageLevelConverter implements Converter<String, MessageLevelEnum> {

    @Override
    public MessageLevelEnum convert(@NotNull String source) {
        return MessageLevelEnum.getByValue(source);
    }
}
