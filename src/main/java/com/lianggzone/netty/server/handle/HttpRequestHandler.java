package com.lianggzone.netty.server.handle;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 处理 Http 请求
 * @author waylau.com
 * @date 2015-3-26 
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { //1
    private String wsUri;
    private static File INDEX;
//    private static final File INDEX2;
//
//    static {
//        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
//        try {
//            String path = location.toURI() + "client/proto/ProtocolModule.proto";
//            path = !path.contains("file:") ? path : path.substring(5);
//            INDEX = new File(path);
//            String path2 = location.toURI() + "client/demo.html";
//            path2 = !path2.contains("file:") ? path2 : path2.substring(5);
//            INDEX2 = new File(path2);
//        } catch (URISyntaxException e) {
//            throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
//        }
//    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.uri())) {
        	System.out.println("是websocket请求吗 : " + wsUri.equalsIgnoreCase(request.uri()));
        	
            ctx.fireChannelRead(request.retain());                  //2  
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);                               //3
            }
            
            INDEX = getFile(ctx,request);
            if(INDEX != null){
	            RandomAccessFile file = new RandomAccessFile(INDEX, "r");//4
	            //RandomAccessFile file ="proto".equalsIgnoreCase(request.uri())? new RandomAccessFile(INDEX, "r"):new RandomAccessFile(INDEX2, "r");//4
	
	            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
	            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
	
	            boolean keepAlive = HttpUtil.isKeepAlive(request);
	
	            if (keepAlive) {                                        //5
	                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
	                response.headers().set(HttpHeaderNames.CONNECTION, HttpUtil.isKeepAlive(request));
	            }
	            ctx.write(response);                    //6
	
	            if (ctx.pipeline().get(SslHandler.class) == null) {     //7
	                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
	            } else {
	                ctx.write(new ChunkedNioFile(file.getChannel()));
	            }
	            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);           //8
//	            if (!keepAlive) {
//	                future.addListener(ChannelFutureListener.CLOSE);        //9
//	            }
	            future.addListener(ChannelFutureListener.CLOSE);    
	            
	            System.out.println("是长连接吗 : " +keepAlive);
	            
	            file.close();
            }
        }
    }
    
    private  File getFile(ChannelHandlerContext ctx,FullHttpRequest request) {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
        	System.out.println("请求文件 : " + request.uri());
            String path = location.toURI() + request.uri();
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX =  new File(path);
            if(INDEX.exists()){
            	return new File(path);
            }else{
            	System.out.println("系统找不到指定的文件 : " + wsUri.equalsIgnoreCase(request.uri()));
            	FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(),HttpResponseStatus.NOT_FOUND);
    	        ctx.write(response);
    	        ctx.flush();
    	        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);    
                future.addListener(ChannelFutureListener.CLOSE); 
            	return null;
            }
            
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
        }
    }
    
    private static void otherUri(ChannelHandlerContext ctx,FullHttpRequest request) {

        String res = "I am OK";
        FullHttpResponse response;
		try {
			response = new DefaultFullHttpResponse(request.protocolVersion(),HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
		
	        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
	        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
	        ctx.write(response);
	        ctx.flush();
	        
	        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);    
            future.addListener(ChannelFutureListener.CLOSE);    
            
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
    	Channel incoming = ctx.channel();
		System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
	}
}

