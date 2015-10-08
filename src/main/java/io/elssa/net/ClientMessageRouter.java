package io.elssa.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMessageRouter extends MessageRouterBase {

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private MessagePipeline pipeline;

    private Map<NetworkEndpoint, Channel> connections = new HashMap<>();

    public ClientMessageRouter() {
        workerGroup = new NioEventLoopGroup();

        pipeline = new MessagePipeline(this);

        bootstrap = new Bootstrap()
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(pipeline);
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

    @Override
    protected void onWritabilityChange(ChannelHandlerContext ctx) {
        Channel chan = ctx.channel();
        synchronized (chan) {
            chan.notifyAll();
        }
    }

    public Transmission sendMessage(
            NetworkEndpoint endpoint, ElssaMessage msg) {
        Channel chan = ensureConnected(endpoint);
        ChannelFuture cf = chan.writeAndFlush(msg);

        if (chan.isWritable() == false) {
            synchronized (chan) {
                while (chan.isWritable() == false) {
                    try {
                        chan.wait();
                    } catch (InterruptedException e) { }
                }
            }
        }

        return new Transmission(cf);
    }

    private Channel ensureConnected(NetworkEndpoint ep) {
        Channel chan = connections.get(ep);
        if (chan == null) {
            ChannelFuture cf = bootstrap.connect(ep.hostname(), ep.port());
            cf.syncUninterruptibly();

            chan = cf.channel();
            connections.put(ep, chan);
        }

        return chan;
    }

    public void shutdown() {
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    public void forceShutdown() {
        workerGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS)
            .syncUninterruptibly();
    }
}
