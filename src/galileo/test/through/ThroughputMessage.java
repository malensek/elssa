package galileo.test.through;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

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
