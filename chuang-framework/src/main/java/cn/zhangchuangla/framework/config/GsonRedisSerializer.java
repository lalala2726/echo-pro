package cn.zhangchuangla.framework.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Redis使用Gson序列化
 *
 * @author Chuang
 */
public class GsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Gson gson;
    private final Class<T> clazz;

    public GsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
        // 创建支持复杂对象序列化的Gson实例
        this.gson = new GsonBuilder()
                .disableHtmlEscaping() // 禁用HTML转义，提高序列化性能
                .serializeNulls() // 序列化null值
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 设置日期格式
                .create();
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return gson.toJson(t).getBytes(DEFAULT_CHARSET);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize object: " + ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            String str = new String(bytes, DEFAULT_CHARSET);
            return gson.fromJson(str, clazz);
        } catch (Exception ex) {
            throw new SerializationException("Could not deserialize object: " + ex.getMessage(), ex);
        }
    }
}
