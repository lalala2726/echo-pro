package cn.zhangchuangla.common.factory;

import cn.zhangchuangla.common.entity.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:53
 */
@Component
@RequiredArgsConstructor
public class UploadServiceFactory {

    private final ApplicationContext applicationContext;

    private final UploadProperties uploadProperties;

}
