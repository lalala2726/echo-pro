package cn.zhangchuangla.message.service.impl;

import cn.zhangchuangla.message.mapper.SysMessageMapper;
import cn.zhangchuangla.message.model.entity.SysMessage;
import cn.zhangchuangla.message.service.SysMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统消息Service实现类
 *
 * @author zhangchuang
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage>
        implements SysMessageService {
}




