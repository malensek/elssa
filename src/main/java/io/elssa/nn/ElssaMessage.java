package io.elssa.nn;

public class ElssaMessage {

    private byte[] payload;

    /**
     * Constructs an ElssaMessage from an array of bytes.
     *
     * @param payload message payload in the form of a byte array.
     */
    public ElssaMessage(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Retrieves the payload for this ElssaMessage.
     *
     * @return the ElssaMessage payload
     */
    public byte[] payload() {
        return payload;
    }
}
