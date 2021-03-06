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
//                    // ????????????????????? ??????????????????????????? ?????????????????????
//                    // 5s ????????????????????? channel ??????????????????????????? IdleState#READER_IDLE ??????
//                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
//                    // ChannelDuplexHandler ??????????????????????????????????????????
//                    ch.pipeline().addLast(new ChannelDuplexHandler() {
//                        // ????????????????????????
//                        @Override
//                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
//                            IdleStateEvent event = (IdleStateEvent) evt;
//                            // ????????????????????????
//                            if (event.state() == IdleState.READER_IDLE) {
//                                log.debug("?????? 5s ?????????????????????");
//                                ctx.channel().close();
//                            }
//                        }
//                    });
//                    pipeline.addLast("HttpServerCodec", new HttpServerCodec());
//                    // ?????????HttpMessage????????????????????????http??????
//                    pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(4096));
//                    // ??????websocket?????????netty???????????????????????????????????????
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
