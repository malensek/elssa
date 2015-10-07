/*
Copyright (c) 2014, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package io.elssa.net;

import io.netty.channel.ChannelHandlerContext;

/**
 * Contains connection-specific information about the source of a
 * {@link ElssaMessage}.
 *
 * @author malensek
 */
public class MessageContext {

    private ChannelHandlerContext channelCtx;

    public MessageContext(ChannelHandlerContext channelCtx) {
        this.channelCtx = channelCtx;
    }

    /**
     * Retrieves the originating endpoint that sent the message associated with
     * this context.
     */
    public NetworkEndpoint remoteEndpoint() {
        return new NetworkEndpoint(channelCtx.channel().remoteAddress());
    }

    /**
     * Retrieves the local destination of the message associated with this
     * context.
     */
    public NetworkEndpoint localEndpoint() {
        return new NetworkEndpoint(channelCtx.channel().localAddress());
    }

    /**
     * Sends a message back to the originator of the message this context
     * belongs to.
     */
    public void sendMessage(ElssaMessage message) {
        channelCtx.channel().writeAndFlush(message);
    }
}
