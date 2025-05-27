package cn.zhangchuangla.message.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户消息阅读服务测试类
 *
 * @author Chuang
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserMessageReadServiceTest {

    @Resource
    private UserMessageReadService userMessageReadService;

    /**
     * 测试真实阅读功能
     */
    @Test
    public void testRealRead() {
        Long userId = 1L;
        Long messageId = 100L;

        // 首次真实阅读
        boolean result = userMessageReadService.realRead(userId, messageId);
        assertTrue(result, "首次真实阅读应该成功");

        // 验证消息已读
        assertTrue(userMessageReadService.isMessageRead(userId, messageId), "消息应该已读");
        assertTrue(userMessageReadService.isMessageRealRead(userId, messageId), "消息应该有真实阅读记录");

        // 获取首次阅读时间
        Date firstReadTime = userMessageReadService.getFirstReadTime(userId, messageId);
        assertNotNull(firstReadTime, "首次阅读时间不应该为空");

        // 等待一段时间后再次阅读
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 再次真实阅读
        boolean secondReadResult = userMessageReadService.realRead(userId, messageId);
        assertTrue(secondReadResult, "再次真实阅读应该成功");

        // 验证首次阅读时间不变
        Date firstReadTimeAfter = userMessageReadService.getFirstReadTime(userId, messageId);
        assertEquals(firstReadTime, firstReadTimeAfter, "首次阅读时间应该保持不变");

        // 验证最后阅读时间更新了
        Date lastReadTime = userMessageReadService.getLastReadTime(userId, messageId);
        assertNotNull(lastReadTime, "最后阅读时间不应该为空");
        assertTrue(lastReadTime.after(firstReadTime) || lastReadTime.equals(firstReadTime),
                "最后阅读时间应该大于等于首次阅读时间");
    }

    /**
     * 测试批量标记已读功能
     */
    @Test
    public void testBatchMarkAsRead() {
        Long userId = 2L;
        List<Long> messageIds = Arrays.asList(200L, 201L, 202L);

        // 批量标记已读
        boolean result = userMessageReadService.batchMarkAsRead(userId, messageIds);
        assertTrue(result, "批量标记已读应该成功");

        // 验证所有消息都已读
        for (Long messageId : messageIds) {
            assertTrue(userMessageReadService.isMessageRead(userId, messageId),
                    "消息 " + messageId + " 应该已读");
            assertFalse(userMessageReadService.isMessageRealRead(userId, messageId),
                    "消息 " + messageId + " 不应该有真实阅读记录");
            assertNull(userMessageReadService.getFirstReadTime(userId, messageId),
                    "消息 " + messageId + " 不应该有首次阅读时间");
        }
    }

    /**
     * 测试批量标记已读后再真实阅读
     */
    @Test
    public void testBatchMarkThenRealRead() {
        Long userId = 3L;
        Long messageId = 300L;

        // 先批量标记已读
        boolean batchResult = userMessageReadService.batchMarkAsRead(userId, Arrays.asList(messageId));
        assertTrue(batchResult, "批量标记已读应该成功");

        // 验证已读但无真实阅读记录
        assertTrue(userMessageReadService.isMessageRead(userId, messageId), "消息应该已读");
        assertFalse(userMessageReadService.isMessageRealRead(userId, messageId), "消息不应该有真实阅读记录");
        assertNull(userMessageReadService.getFirstReadTime(userId, messageId), "不应该有首次阅读时间");

        // 再真实阅读
        boolean realReadResult = userMessageReadService.realRead(userId, messageId);
        assertTrue(realReadResult, "真实阅读应该成功");

        // 验证现在有真实阅读记录了
        assertTrue(userMessageReadService.isMessageRealRead(userId, messageId), "现在应该有真实阅读记录");
        assertNotNull(userMessageReadService.getFirstReadTime(userId, messageId), "现在应该有首次阅读时间");
        assertNotNull(userMessageReadService.getLastReadTime(userId, messageId), "现在应该有最后阅读时间");
    }

    /**
     * 测试标记未读功能
     */
    @Test
    public void testUnread() {
        Long userId = 4L;
        Long messageId = 400L;

        // 先真实阅读
        userMessageReadService.realRead(userId, messageId);
        assertTrue(userMessageReadService.isMessageRead(userId, messageId), "消息应该已读");

        // 标记未读
        boolean unreadResult = userMessageReadService.unread(userId, messageId);
        assertTrue(unreadResult, "标记未读应该成功");

        // 验证消息未读，但阅读时间记录仍然存在
        assertFalse(userMessageReadService.isMessageRead(userId, messageId), "消息应该未读");
        assertNotNull(userMessageReadService.getFirstReadTime(userId, messageId), "首次阅读时间应该保留");
        assertNotNull(userMessageReadService.getLastReadTime(userId, messageId), "最后阅读时间应该保留");
    }

    /**
     * 测试获取已读消息ID列表
     */
    @Test
    public void testGetReadMessageIds() {
        Long userId = 5L;
        List<Long> allMessageIds = Arrays.asList(500L, 501L, 502L, 503L);
        List<Long> readMessageIds = Arrays.asList(500L, 502L);

        // 真实阅读部分消息
        userMessageReadService.realRead(userId, readMessageIds);

        // 获取已读消息ID列表
        List<Long> actualReadIds = userMessageReadService.getReadMessageIds(userId, allMessageIds);

        assertEquals(readMessageIds.size(), actualReadIds.size(), "已读消息数量应该正确");
        assertTrue(actualReadIds.containsAll(readMessageIds), "应该包含所有已读消息");
    }
}
