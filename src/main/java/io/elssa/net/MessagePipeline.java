package io.elssa.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class MessagePipeline extends ChannelInitializer<SocketChannel> {

    private InboundHandler inboundHandler;
    private MessageEncoder encoder;

    public MessagePipeline(MessageRouterBase router) {
        inboundHandler = new InboundHandler(router);
        encoder = new MessageEncoder();
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        /* Inbound: */
        ch.pipeline().addLast(new MessageDecoder()); /* (stateful) */
        ch.pipeline().addLast(inboundHandler);

        /* Outbound: */
        ch.pipeline().addLast(encoder);
    }

}
