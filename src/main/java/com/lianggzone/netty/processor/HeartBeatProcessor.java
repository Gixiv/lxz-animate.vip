package com.lianggzone.netty.processor;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.stereotype.Component;

import com.lianggzone.netty.entity.ProtocolModule.CommonProtocol;

/**
 * @author 卢锡仲
 * @since 0.1
 */
@Component
public class HeartBeatProcessor implements IProcessor {

    @Override
    public void excute(ChannelHandlerContext ctx, CommonProtocol msg) throws Exception {
        System.out.println("返回心跳包给客户端");
        
    }
}
