package cn.zhangchuangla.system.message.converter;

import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 */
@Component
public class MessageTypeConverter implements Converter<String, MessageTypeEnum> {
    @Override
    public MessageTypeEnum convert(@NotNull String source) {
        return MessageTypeEnum.getByValue(source);
    }
}
