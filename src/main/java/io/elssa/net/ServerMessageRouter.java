package io.elssa.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerMessageRouter extends MessageRouterBase {

    private static final Logger logger
        = LoggerFactory.getLogger(ServerMessageRouter.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private MessagePipeline pipeline;

    private Map<Integer, ChannelFuture> ports = new HashMap<>();

    public ServerMessageRouter() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(4);

        pipeline = new MessagePipeline(this);

        bootstrap = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(pipeline)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
            .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
    }

    public ServerMessageRouter(int readBufferSize, int maxWriteQueueSize) {
        this();
    }

    public void listen(int port) {
        ChannelFuture cf = bootstrap.bind(port).syncUninterruptibly();
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
}
