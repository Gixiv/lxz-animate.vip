package com.lianggzone.netty.server.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.lianggzone.netty.entity.ProtocolModule;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

@Component
@ChannelHandler.Sharable
public class TcpServerHandler extends SimpleChannelInboundHandler<ProtocolModule.CommonProtocol> {
	
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	 // 一个 ChannelGroup 代表一个队伍
    private static Map<Integer, ChannelGroup> channelGroupMap = new HashMap<>();
    /**
     * 客户端与服务端会话连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("客户端与服务端会话连接成功");
        
        Channel incoming = ctx.channel();
        channels.add(incoming);
    }

    /**
     * 服务端接收到客户端消息
     */
    @Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolModule.CommonProtocol msg) throws Exception {
        System.out.println(msg);
        //ctx.write(msg);
		Channel incoming = ctx.channel();
		System.out.println(channels);
		for (Channel channel : channels) {
            if (channel != incoming){
            	//channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + msg);
                channel.writeAndFlush(msg);
            } else {
            	channel.writeAndFlush(msg);
            	
            	System.out.println(msg.getLiveHeader().getLiveId());
            }
        }
	}	

    /**
     * 服务端监听到客户端异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务端监听到客户端异常");
    }
   
    /**
     * 客户端与服务端会话连接断开
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	super.channelInactive(ctx);
    	System.out.println("客户端与服务端会话连接断开");
    } 
}