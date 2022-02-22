package com.fernando.server;

import com.fernando.config.Config;
import com.fernando.protocol.MessageCodecSharable;
import com.fernando.protocol.ProcotolFrameDecoder;
import com.fernando.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new ProcotolFrameDecoder());
                    pipeline.addLast(LOGGING_HANDLER);
                    pipeline.addLast(MESSAGE_CODEC);
//                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
//                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
//                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
//                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
//                    ch.pipeline().addLast(new ChannelDuplexHandler() {
//                        // 用来触发特殊事件
//                        @Override
//                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
//                            IdleStateEvent event = (IdleStateEvent) evt;
//                            // 触发了读空闲事件
//                            if (event.state() == IdleState.READER_IDLE) {
//                                log.debug("已经 5s 没有读到数据了");
//                                ctx.channel().close();
//                            }
//                        }
//                    });
//                    pipeline.addLast("HttpServerCodec", new HttpServerCodec());
//                    // 将多个HttpMessage组装成一个完整的http请求
//                    pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(4096));
//                    // 处理websocket专用的netty处理器，参数指定服务端地址
//                    pipeline.addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/myWebSocket"));
                    pipeline.addLast(LOGIN_HANDLER);
                    pipeline.addLast(CHAT_HANDLER);
                    pipeline.addLast(GROUP_CREATE_HANDLER);
                    pipeline.addLast(GROUP_JOIN_HANDLER);
                    pipeline.addLast(GROUP_MEMBERS_HANDLER);
                    pipeline.addLast(GROUP_QUIT_HANDLER);
                    pipeline.addLast(GROUP_CHAT_HANDLER);
                    pipeline.addLast(QUIT_HANDLER);
                }
            });
            Channel channel = serverBootstrap.bind(Config.getServerPort()).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
