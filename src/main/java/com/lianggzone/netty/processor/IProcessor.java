package com.lianggzone.netty.processor;

import io.netty.channel.ChannelHandlerContext;

import com.lianggzone.netty.entity.ProtocolModule;

/**
 * @author 卢锡仲
 * @since 0.1
 */
public interface IProcessor {

    /**
     * 执行处理任务	  
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void excute(ChannelHandlerContext ctx, ProtocolModule.CommonProtocol msg) throws Exception;
}
