package io.elssa.nn;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class MessageDecoder extends ReplayingDecoder<DecoderState> {

    private int length;

    public MessageDecoder() {
        super(DecoderState.READ_LENGTH);
    }

    @Override
    protected void decode(
            ChannelHandlerContext ctx, ByteBuf buf, List<Object> out)
    throws Exception {

    switch (state()) {
        case READ_LENGTH:
            length = buf.readInt();
            checkpoint(DecoderState.READ_CONTENT);
            /* Fall through to the next state; the entire message may be
             * available */

        case READ_CONTENT:
            ByteBuf frame = buf.readBytes(length);
            checkpoint(DecoderState.READ_LENGTH);
            out.add(frame);
            break;

        default:
            throw new Error("Unknown decoder state");
    }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
