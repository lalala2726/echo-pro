package cn.zhangchuangla.message.constant;

/**
 * 消息常量
 *
 * @author Chuang
 * <p>
 * created on 2025/5/27 11:38
 */
public class MessageConstants {

    /**
     * 状态常量
     */
    public interface StatusConstants {
        Integer MESSAGE_IS_READ = 1;
        Integer MESSAGE_UN_READ = 0;
    }

    public interface MessageTypeConstants {
        Integer SYSTEM_MESSAGE = 1;
        Integer NOTICE_MESSAGE = 2;
        Integer GENERAL_MESSAGE = 3;
        Integer ANNOUNCEMENT_MESSAGE = 4;
        Integer USER_MESSAGE = 5;
    }

}
