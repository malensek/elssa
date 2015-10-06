package io.elssa.net;

public class ElssaMessage {

    private byte[] payload;
    private MessageContext context;

    /**
     * Constructs an ElssaMessage from an array of bytes.
     *
     * @param payload message payload in the form of a byte array.
     */
    public ElssaMessage(byte[] payload) {
        this.payload = payload;
    }

    public ElssaMessage(byte[] payload, MessageContext context) {
        this(payload);
        this.context = context;
    }

    /**
     * Retrieves the payload for this ElssaMessage.
     *
     * @return the ElssaMessage payload
     */
    public byte[] payload() {
        return payload;
    }

    public MessageContext context() {
        return context;
    }
}
