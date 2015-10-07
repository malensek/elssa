package io.elssa.test.through;

import java.io.IOException;

import io.elssa.event.Event;
import io.elssa.serialization.SerializationInputStream;
import io.elssa.serialization.SerializationOutputStream;

public class ThroughputMessage implements Event {

    private byte[] payload;

    public ThroughputMessage(int size) {
        payload = new byte[size];
    }

    public byte[] getPayload() {
        return payload;
    }

    @Deserialize
    public ThroughputMessage(SerializationInputStream in)
    throws IOException {
        payload = in.readField();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeField(payload);
    }
}
