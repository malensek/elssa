package io.elssa.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMessageRouter {

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;

    private Map<NetworkEndpoint, ChannelFuture> connections = new HashMap<>();

    public ClientMessageRouter() {
        workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new MessageEncoder());
            }
        });
    }

    public ClientMessageRouter(int readBufferSize, int maxWriteQueueSize) {
        this();
    }

    /**
     * Sends a message to multiple network destinations.
     */
    public List<Transmission> broadcastMessage(
            Iterable<NetworkEndpoint> endpoints, ElssaMessage message) {
        List<Transmission> transmissions = new ArrayList<>();
        for (NetworkEndpoint endpoint : endpoints) {
            Transmission trans = sendMessage(endpoint, message);
            transmissions.add(trans);
        }
        return transmissions;
    }

    public Transmission sendMessage(
            NetworkEndpoint endpoint, ElssaMessage msg) {
        ChannelFuture cf = ensureConnected(endpoint);
        cf.channel().writeAndFlush(msg);
        return new Transmission(cf);
    }

    private ChannelFuture ensureConnected(NetworkEndpoint ep) {
        ChannelFuture cf = connections.get(ep);
        if (cf == null) {
            ChannelFuture channel = bootstrap.connect(ep.hostname(), ep.port());
            channel.syncUninterruptibly();
            connections.put(ep, channel);
            cf = channel;
        }

        return cf;
    }

    public void shutdown() {
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    public void forceShutdown() {
        workerGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS)
            .syncUninterruptibly();
    }

    public static void main(String[] args) throws Exception {
        ClientMessageRouter cmr = new ClientMessageRouter();
        cmr.sendMessage(new NetworkEndpoint("localhost", 5555), new ElssaMessage(new byte[2024]));
    }
}
