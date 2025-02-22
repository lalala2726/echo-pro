package cn.zhangchuangla.common.utils.http;

import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 高效HTTP工具类（基于OkHttp实现）
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 20:40
 */
public class HttpUtils {

    // 默认共享的OkHttpClient实例（支持连接池复用）
    private static volatile OkHttpClient defaultClient;

    // 私有化构造方法
    private HttpUtils() {
    }

    /**
     * 获取默认的OkHttpClient实例（双重校验锁单例模式）
     */
    public static OkHttpClient getDefaultClient() {
        if (defaultClient == null) {
            synchronized (HttpUtils.class) {
                if (defaultClient == null) {
                    defaultClient = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)    // 连接超时
                            .readTimeout(30, TimeUnit.SECONDS)       // 读取超时
                            .writeTimeout(30, TimeUnit.SECONDS)      // 写入超时
                            .retryOnConnectionFailure(true)          // 自动重试
                            .build();
                }
            }
        }
        return defaultClient;
    }

    /**
     * 设置自定义的OkHttpClient（用于全局配置修改）
     *
     * @param client 自定义的OkHttpClient实例
     */
    public static void setCustomClient(@NotNull OkHttpClient client) {
        defaultClient = client;
    }

    /**
     * 执行HTTP请求并返回字符串结果（通用方法）
     *
     * @param request 构建好的请求对象
     * @return 响应体字符串
     * @throws IOException 网络异常或协议异常时抛出
     */
    public static String execute(@NotNull Request request) throws IOException {
        try (Response response = getDefaultClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP请求失败，状态码：" + response.code());
            }
            ResponseBody body = response.body();
            return body != null ? body.string() : "";
        }
    }

    /**
     * GET请求
     *
     * @param url     请求地址
     * @param headers 请求头（可为null）
     * @return 响应体字符串
     */
    public static String get(@NotNull String url, @Nullable Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    /**
     * POST请求（发送JSON数据）
     *
     * @param url     请求地址
     * @param headers 请求头（可为null）
     * @param json    JSON格式请求体
     * @return 响应体字符串
     */
    public static String postJson(@NotNull String url, @Nullable Map<String, String> headers,
                                  @NotNull String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        return post(url, headers, body);
    }

    /**
     * POST请求（发送表单数据）
     *
     * @param url     请求地址
     * @param headers 请求头（可为null）
     * @param params  表单参数
     * @return 响应体字符串
     */
    public static String postForm(@NotNull String url, @Nullable Map<String, String> headers,
                                  @NotNull Map<String, String> params) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        params.forEach(formBuilder::add);
        return post(url, headers, formBuilder.build());
    }

    /**
     * 通用POST请求
     *
     * @param url     请求地址
     * @param headers 请求头（可为null）
     * @param body    请求体
     * @return 响应体字符串
     */
    public static String post(@NotNull String url, @Nullable Map<String, String> headers,
                              @NotNull RequestBody body) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    /**
     * DELETE请求
     *
     * @param url     请求地址
     * @param headers 请求头（可为null）
     * @return 响应体字符串
     */
    public static String delete(@NotNull String url, @Nullable Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    /**
     * PUT请求（发送任意类型数据）
     *
     * @param url     请求地址
     * @param headers 请求头（可为null）
     * @param body    请求体
     * @return 响应体字符串
     */
    public static String put(@NotNull String url, @Nullable Map<String, String> headers,
                             @NotNull RequestBody body) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    /**
     * 添加请求头
     *
     * @param builder Request.Builder对象
     * @param headers 请求头Map
     */
    private static void addHeaders(@NotNull Request.Builder builder, @Nullable Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
    }

    /**
     * 文件上传专用方法
     *
     * @param url         请求地址
     * @param headers     请求头（可为null）
     * @param fieldName   表单字段名
     * @param fileName    文件名
     * @param mediaType   文件MIME类型
     * @param fileBytes   文件字节数组
     * @param otherParams 其他表单参数
     * @return 响应体字符串
     */
    public static String upload(String url, Map<String, String> headers, String fieldName,
                                String fileName, MediaType mediaType, byte[] fileBytes,
                                Map<String, String> otherParams) throws IOException {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加文件
        bodyBuilder.addFormDataPart(fieldName, fileName, new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(fileBytes);
            }
        });

        // 添加其他参数
        if (otherParams != null) {
            otherParams.forEach(bodyBuilder::addFormDataPart);
        }

        return post(url, headers, bodyBuilder.build());
    }
}
