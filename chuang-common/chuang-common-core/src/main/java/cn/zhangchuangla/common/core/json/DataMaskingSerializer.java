package cn.zhangchuangla.common.core.json;

import cn.zhangchuangla.common.core.annotation.DataMasking;
import cn.zhangchuangla.common.core.utils.DataMaskingUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;

/**
 * 数据脱敏 JSON 序列化器
 *
 * @author Chuang
 * @since 2025/4/23
 */
public class DataMaskingSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private DataMasking dataMasking;

    public DataMaskingSerializer() {
    }

    public DataMaskingSerializer(DataMasking dataMasking) {
        this.dataMasking = dataMasking;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        if (dataMasking != null) {
            String maskedValue = DataMaskingUtils.mask(value, dataMasking);
            gen.writeString(maskedValue);
        } else {
            gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null) {
            DataMasking annotation = property.getAnnotation(DataMasking.class);
            if (annotation == null) {
                annotation = property.getContextAnnotation(DataMasking.class);
            }
            if (annotation != null) {
                return new DataMaskingSerializer(annotation);
            }
        }
        return this;
    }
}
