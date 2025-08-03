package cn.zhangchuangla.system.core.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/3 03:25
 */
@Data
@Schema(name = "用户安全日志", description = "展示用户最近的安全日志信息")
public class UserSecurityLog {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 日志标题
     */
    private String title;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作区域
     */
    private String operationRegion;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作时间
     */
    private Date operationTime;
}
