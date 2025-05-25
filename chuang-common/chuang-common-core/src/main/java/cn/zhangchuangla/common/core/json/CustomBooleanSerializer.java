package cn.zhangchuangla.common.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 自定义 Boolean 类型序列化器，如果是false，则不输出任何内容
 * 使用方法 @JsonSerialize(using = CustomBooleanSerializer.class)
 *
 * @author Chuang
 * <p>
 * <p>
 * created on 2025/3/31 13:56
 */
public class CustomBooleanSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null && value) {
            jsonGenerator.writeBoolean(true);
        } else {
            // 当值为null或false时，不输出任何内容
            jsonGenerator.writeNull();
        }
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Boolean value) {
        return value == null || !value;
    }
}
