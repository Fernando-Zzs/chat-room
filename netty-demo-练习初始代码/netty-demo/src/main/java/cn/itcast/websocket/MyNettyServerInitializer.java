package cn.itcast.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class MyNettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        // 将多个HttpMessage组装成一个完整的http请求
        pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(4096));
        // 处理websocket专用的netty处理器，参数指定服务端地址
        pipeline.addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/myWebSocket"));
        pipeline.addLast("MyNettyServerHandler", new MyNettyServerHandler());
    }
}
