package io.elssa.net;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class InboundHandler extends ChannelInboundHandlerAdapter {

    private MessageRouterBase router;

    public InboundHandler(MessageRouterBase router) {
        this.router = router;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        NetworkEndpoint endpoint = new NetworkEndpoint(
                addr.getHostName(), addr.getPort());

        router.onConnnect(endpoint);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        NetworkEndpoint endpoint = new NetworkEndpoint(
                addr.getHostName(), addr.getPort());

        router.onDisconnect(endpoint);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    throws Exception {
        router.onWritabilityChange(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ElssaMessage) {
            router.onMessage((ElssaMessage) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //TODO: this should raise an event to clients
        System.out.println("Inbound channel exception");
        cause.printStackTrace();
    }
}
