package io.elssa.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerMessageRouter extends MessageRouterBase {

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
            .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public ServerMessageRouter(int readBufferSize, int maxWriteQueueSize) {
        this();
    }

    /**
     * Begins listening for incoming messages on the specified port. When this
     * method returns, the server socket is open and ready to accept
     * connections.
     *
     * @param port The port to listen on
     */
    public void listen(int port) {
        ChannelFuture cf = bootstrap.bind(port).syncUninterruptibly();
        ports.put(port, cf);
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
