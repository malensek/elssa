package io.elssa.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerMessageRouter {

    private static final Logger logger
        = LoggerFactory.getLogger(ServerMessageRouter.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap boot;

    private MessageEncoder encoder;
    private ServerInboundHandler inboundHandler;

    private Map<Integer, ChannelFuture> ports = new HashMap<>();

    public ServerMessageRouter() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        encoder = new MessageEncoder();
        inboundHandler = new ServerInboundHandler();

        boot = new ServerBootstrap();
        boot.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     /* Inbound: */
                     ch.pipeline().addLast(new MessageDecoder());
                     ch.pipeline().addLast(inboundHandler);

                     /* Outbound: */
                     ch.pipeline().addLast(encoder);
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public ServerMessageRouter(int readBufferSize, int maxWriteQueueSize) {
        this();
    }

    public void listen(int port) {
        ChannelFuture cf = boot.bind(port).syncUninterruptibly();
        ports.put(port, cf);
        logger.info("Listening on port {}", port);
    }

    public void close(int port) {
        ChannelFuture cf = ports.get(port);
        if (cf == null) {
            return;
        }
        ports.remove(port);
        cf.channel().disconnect().syncUninterruptibly();
    }

    /**
     * Closes the server socket channel and stops processing incoming
     * messages.
     */
    public void shutdown() throws IOException {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        for (ChannelFuture cf : ports.values()) {
            cf.channel().close().syncUninterruptibly();
        }
    }

    /**
     * Adds a message listener (consumer) to this MessageRouter.  Listeners
     * receive messages that are published by this MessageRouter.
     *
     * @param listener {@link MessageListener} that will consume messages
     * published by this MessageRouter.
     */
    public void addListener(MessageListener listener) {
        inboundHandler.addListener(listener);
    }

    public void removeListener(MessageListener listener) {
        inboundHandler.removeListener(listener);
    }

}
