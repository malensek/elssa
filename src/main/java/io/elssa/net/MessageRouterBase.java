package io.elssa.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;

public abstract class MessageRouterBase {

    private List<MessageListener> listeners
        = Collections.synchronizedList(new ArrayList<>());

    /**
     * Adds a message listener (consumer) to this MessageRouter.  Listeners
     * receive messages that are published by this MessageRouter.
     *
     * @param listener {@link MessageListener} that will consume messages
     * published by this MessageRouter.
     */
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    protected void onWritabilityChange(ChannelHandlerContext ctx) {

    }

    protected void onConnnect(NetworkEndpoint endpoint) {
        for (MessageListener listener : listeners) {
            listener.onConnect(endpoint);
        }
    }

    protected void onDisconnect(NetworkEndpoint endpoint) {
        for (MessageListener listener : listeners) {
            listener.onDisconnect(endpoint);
        }
    }

    protected void onMessage(ElssaMessage msg) {
        for (MessageListener listener : listeners) {
            listener.onMessage(msg);
        }
    }
}
