package io.elssa.net;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerInboundHandler extends ChannelInboundHandlerAdapter {

    final Logger logger = LoggerFactory.getLogger(ServerInboundHandler.class);

    private List<MessageListener> listeners = new ArrayList<>();

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        NetworkEndpoint endpoint = new NetworkEndpoint(
                addr.getHostName(), addr.getPort());
        logger.info("Accepted connection: {}", endpoint.toString());

        for (MessageListener listener : listeners) {
            listener.onConnect(endpoint);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        NetworkEndpoint endpoint = new NetworkEndpoint(
                addr.getHostName(), addr.getPort());
        logger.info("Terminating connection: {}", endpoint);

        for (MessageListener listener : listeners) {
            listener.onDisconnect(endpoint);
        }
    }

    /**
     * Dispatches a message to all listening consumers.
     *
     * @param message {@link ElssaMessage} to dispatch.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ElssaMessage) {
            for (MessageListener listener : listeners) {
                listener.onMessage((ElssaMessage) msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("Inbound channel exception", cause);
    }
}
