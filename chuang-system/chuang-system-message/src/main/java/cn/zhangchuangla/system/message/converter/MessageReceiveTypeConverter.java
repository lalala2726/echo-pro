package cn.zhangchuangla.system.message.converter;

import cn.zhangchuangla.system.message.enums.MessageReceiveTypeEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/10 15:06
 */
@Component
public class MessageReceiveTypeConverter implements Converter<String, MessageReceiveTypeEnum> {

    @Override
    public MessageReceiveTypeEnum convert(@NotNull String source) {
        return MessageReceiveTypeEnum.getByValue(source);
    }
}
